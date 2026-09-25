package com.example.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class ZenithNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        NotificationBadgeManager.setConnected(true)
        refreshBadges()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        NotificationBadgeManager.setConnected(false)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        refreshBadges()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        refreshBadges()
    }

    private fun refreshBadges() {
        try {
            val active = activeNotifications ?: return
            val counts = mutableMapOf<String, Int>()
            for (notification in active) {
                // Ignore ongoing notifications like music playback or persistent notifications if flagged
                val isOngoing = notification.isOngoing
                if (!isOngoing) {
                    val pkg = notification.packageName
                    counts[pkg] = (counts[pkg] ?: 0) + 1
                }
            }
            NotificationBadgeManager.setAllCounts(counts)
        } catch (_: Exception) {
            // Ignore security or lifecycle exceptions
        }
    }
}
