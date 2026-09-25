package com.example.data.model

enum class WidgetSize(val displayName: String, val shortLabel: String) {
    COMPACT("Compact", "S"),
    STANDARD("Standard", "M"),
    EXPANDED("Expanded", "L")
}

enum class WidgetType(val id: String, val displayName: String, val description: String, val defaultSize: WidgetSize) {
    CLOCK("CLOCK", "Minimalist Clock & Date", "Clean typography with battery and date info", WidgetSize.STANDARD),
    GLANCE("GLANCE", "Smart Glance Bar", "Contextual greeting, daily overview and weather glance", WidgetSize.STANDARD),
    SYSTEM_STATS("SYSTEM_STATS", "Device Performance Monitor", "RAM, Battery and Storage status with zero background drain", WidgetSize.STANDARD),
    QUICK_ACTIONS("QUICK_ACTIONS", "Quick Actions Strip", "Immediate shortcuts for Flashlight, Camera, Settings, and Tools", WidgetSize.STANDARD),
    QUICK_NOTE("QUICK_NOTE", "Minimal Scratchpad", "Direct home screen note for quick thoughts", WidgetSize.STANDARD),
    COUNTDOWN("COUNTDOWN", "Event Countdown", "Count down days and hours to an upcoming milestone or trip", WidgetSize.STANDARD),
    HABIT_TRACKER("HABIT_TRACKER", "Daily Habit Tracker", "Track daily goals and check them off directly on desktop", WidgetSize.STANDARD),
    APP_CLUSTER("APP_CLUSTER", "App Cluster Dock", "Group 4 favorite apps in a compact mini-launch card", WidgetSize.STANDARD),
    CUSTOM_QUOTE("CUSTOM_QUOTE", "Daily Affirmation & Quote", "Inspirational reminder or custom motto for focus", WidgetSize.STANDARD),
    WEB_SHORTCUT("WEB_SHORTCUT", "Web Search & Bookmarks", "Direct search jump to Google, Reddit, GitHub or custom site", WidgetSize.COMPACT)
}

data class WidgetConfig(
    val id: String,
    val type: WidgetType,
    val isEnabled: Boolean = true,
    val sortOrder: Int = 0,
    val size: WidgetSize = WidgetSize.STANDARD,
    val title: String = "",
    val customData: String = ""
)
