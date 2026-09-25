package com.example.service

import android.app.Notification
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.media.session.MediaSession
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class ZenithNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        NotificationBadgeManager.setConnected(true)
        ZenithMediaManager.onNotificationListenerConnected(this)
        refreshBadges()
        scanActiveMediaNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        NotificationBadgeManager.setConnected(false)
        ZenithMediaManager.onNotificationListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn != null) {
            processMediaNotification(sbn)
        }
        refreshBadges()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        refreshBadges()
        scanActiveMediaNotifications()
    }

    private fun scanActiveMediaNotifications() {
        try {
            val active = activeNotifications
            if (active.isNullOrEmpty()) {
                ZenithMediaManager.resetToNoTrack()
                return
            }
            var foundMedia = false
            for (sbn in active) {
                if (processMediaNotification(sbn)) {
                    foundMedia = true
                }
            }
            if (!foundMedia) {
                ZenithMediaManager.resetToNoTrack()
            }
        } catch (_: Exception) {}
    }

    private fun processMediaNotification(sbn: StatusBarNotification): Boolean {
        try {
            val notification = sbn.notification ?: return false
            val extras = notification.extras ?: return false
            val pkg = sbn.packageName

            // Extract MediaSession token if available
            val token = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable(Notification.EXTRA_MEDIA_SESSION, MediaSession.Token::class.java)
            } else {
                @Suppress("DEPRECATION")
                extras.getParcelable(Notification.EXTRA_MEDIA_SESSION) as? MediaSession.Token
            }

            if (token != null) {
                ZenithMediaManager.onMediaSessionTokenReceived(applicationContext, token, pkg)
            }

            val isMediaCategory = notification.category == Notification.CATEGORY_TRANSPORT
            val isKnownMusicApp = pkg.contains("spotify", ignoreCase = true) ||
                    pkg.contains("music", ignoreCase = true) ||
                    pkg.contains("audio", ignoreCase = true)

            if (token != null || isMediaCategory || isKnownMusicApp) {
                val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
                val artist = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
                val album = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()

                var artBitmap: Bitmap? = null
                try {
                    val largeIcon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        notification.getLargeIcon()
                    } else {
                        null
                    }
                    if (largeIcon != null) {
                        val drawable = largeIcon.loadDrawable(this)
                        if (drawable is BitmapDrawable) {
                            artBitmap = drawable.bitmap
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        artBitmap = extras.getParcelable(Notification.EXTRA_LARGE_ICON) as? Bitmap
                    }
                } catch (_: Exception) {}

                val isPlaying = sbn.isOngoing

                if (!title.isNullOrBlank()) {
                    ZenithMediaManager.onMediaNotificationExtracted(
                        title = title,
                        artist = artist,
                        album = album,
                        artBitmap = artBitmap,
                        pkg = pkg,
                        isPlaying = isPlaying
                    )
                    return true
                }
            }
        } catch (_: Exception) {}
        return false
    }

    private fun refreshBadges() {
        try {
            val active = activeNotifications ?: return
            val counts = mutableMapOf<String, Int>()
            for (notification in active) {
                val isOngoing = notification.isOngoing
                if (!isOngoing) {
                    val pkg = notification.packageName
                    counts[pkg] = (counts[pkg] ?: 0) + 1
                }
            }
            NotificationBadgeManager.setAllCounts(counts)
        } catch (_: Exception) {}
    }
}
