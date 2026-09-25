package com.example.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.collection.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object IconCacheManager {
    // Cache up to 200 app icons in memory for smooth 120Hz scrolling with zero garbage collection spikes
    private val memoryCache = LruCache<String, ImageBitmap>(200)

    suspend fun getAppIcon(context: Context, packageName: String): ImageBitmap? {
        val cached = memoryCache.get(packageName)
        if (cached != null) {
            return cached
        }

        return withContext(Dispatchers.Default) {
            try {
                val pm = context.packageManager
                val drawable = pm.getApplicationIcon(packageName)
                val bitmap = drawableToBitmap(drawable)
                val imageBitmap = bitmap.asImageBitmap()
                memoryCache.put(packageName, imageBitmap)
                imageBitmap
            } catch (_: Exception) {
                null
            }
        }
    }

    fun getFromCache(packageName: String): ImageBitmap? {
        return memoryCache.get(packageName)
    }

    fun putInCache(packageName: String, bitmap: ImageBitmap) {
        memoryCache.put(packageName, bitmap)
    }

    fun clear() {
        memoryCache.evictAll()
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 144
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 144

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
