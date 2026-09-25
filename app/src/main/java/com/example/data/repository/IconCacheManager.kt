package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.collection.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

object IconCacheManager {
    // Cache up to 300 app icons in memory
    private val memoryCache = LruCache<String, ImageBitmap>(300)
    private val lock = Any()

    // Concurrency throttle: maximum 4 concurrent IPC/bitmap decodes to prevent thread pool & Binder exhaustion
    private val decodeSemaphore = Semaphore(4)

    suspend fun getAppIcon(context: Context, packageName: String): ImageBitmap? {
        // Synchronized cache check
        synchronized(lock) {
            val cached = memoryCache.get(packageName)
            if (cached != null) return cached
        }

        return withContext(Dispatchers.IO) {
            try {
                decodeSemaphore.withPermit {
                    // Double-check cache inside permit
                    synchronized(lock) {
                        val cached = memoryCache.get(packageName)
                        if (cached != null) return@withPermit cached
                    }

                    val pm = context.packageManager
                    val drawable = pm.getApplicationIcon(packageName)
                    val bitmap = drawableToBitmap(drawable) ?: return@withPermit null
                    val imageBitmap = bitmap.asImageBitmap()

                    synchronized(lock) {
                        memoryCache.put(packageName, imageBitmap)
                    }
                    imageBitmap
                }
            } catch (_: Throwable) {
                // Safely catch any OutOfMemoryError, SecurityException, DeadObjectException or IPC errors
                null
            }
        }
    }

    fun getFromCache(packageName: String): ImageBitmap? {
        synchronized(lock) {
            return memoryCache.get(packageName)
        }
    }

    fun putInCache(packageName: String, bitmap: ImageBitmap) {
        synchronized(lock) {
            memoryCache.put(packageName, bitmap)
        }
    }

    fun clear() {
        synchronized(lock) {
            memoryCache.evictAll()
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        return try {
            if (drawable is BitmapDrawable && drawable.bitmap != null) {
                val orig = drawable.bitmap
                if (orig.width in 1..144 && orig.height in 1..144) {
                    return orig
                }
                return Bitmap.createScaledBitmap(orig, 128, 128, true)
            }

            // Always render to a fixed, compact 128x128 size for low memory footprint and high speed
            val size = 128
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, size, size)
            drawable.draw(canvas)
            bitmap
        } catch (_: Throwable) {
            null
        }
    }
}
