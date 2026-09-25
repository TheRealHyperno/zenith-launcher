package com.example.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object NotificationBadgeManager {
    private val _badgeCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val badgeCounts: StateFlow<Map<String, Int>> = _badgeCounts.asStateFlow()

    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    fun updateCount(packageName: String, count: Int) {
        _badgeCounts.update { current ->
            val updated = current.toMutableMap()
            if (count <= 0) {
                updated.remove(packageName)
            } else {
                updated[packageName] = count
            }
            updated
        }
    }

    fun setAllCounts(counts: Map<String, Int>) {
        _badgeCounts.value = counts
    }

    fun setConnected(connected: Boolean) {
        _isServiceConnected.value = connected
    }

    fun simulateTestBadge(packageName: String, count: Int = 3) {
        updateCount(packageName, count)
    }

    fun clearBadge(packageName: String) {
        updateCount(packageName, 0)
    }
}
