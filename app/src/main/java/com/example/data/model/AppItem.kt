package com.example.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class AppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val customLabel: String? = null,
    val isPinnedHome: Boolean = false,
    val homeGridIndex: Int = -1,
    val isPinnedDock: Boolean = false,
    val dockIndex: Int = -1,
    val isHidden: Boolean = false,
    val launchCount: Int = 0,
    val lastLaunchTime: Long = 0L,
    val badgeCount: Int = 0,
    val category: AppCategory = AppCategory.ALL,
    val isSystemApp: Boolean = false
) {
    val displayName: String
        get() = customLabel?.takeIf { it.isNotBlank() } ?: label
}
