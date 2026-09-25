package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_configs")
data class AppConfigEntity(
    @PrimaryKey
    val packageName: String,
    val customLabel: String? = null,
    val isPinnedHome: Boolean = false,
    val homeGridIndex: Int = -1,
    val isPinnedDock: Boolean = false,
    val dockIndex: Int = -1,
    val isHidden: Boolean = false,
    val launchCount: Int = 0,
    val lastLaunchTime: Long = 0L,
    val customCategory: String? = null
)

@Entity(tableName = "gesture_configs")
data class GestureConfigEntity(
    @PrimaryKey
    val gestureTrigger: String, // e.g., "SWIPE_UP", "SWIPE_DOWN", "DOUBLE_TAP", "PINCH_IN", "TWO_FINGER_SWIPE_UP"
    val actionType: String,     // e.g., "APP_DRAWER", "SEARCH", "NOTIFICATIONS", "SETTINGS", "LAUNCH_APP", "LOCK_SCREEN"
    val targetPackage: String? = null
)

@Entity(tableName = "widget_configs")
data class WidgetConfigEntity(
    @PrimaryKey
    val widgetId: String,
    val isEnabled: Boolean = true,
    val sortOrder: Int = 0,
    val styleType: String = "DEFAULT"
)

@Entity(tableName = "widget_instances")
data class WidgetInstanceEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val isEnabled: Boolean = true,
    val sortOrder: Int = 0,
    val size: String = "STANDARD", // "COMPACT", "STANDARD", "EXPANDED"
    val title: String = "",
    val customData: String = ""
)

@Entity(tableName = "quick_notes")
data class QuickNoteEntity(
    @PrimaryKey
    val id: Int = 1,
    val content: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
