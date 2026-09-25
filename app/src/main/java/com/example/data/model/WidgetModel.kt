package com.example.data.model

enum class WidgetType(val id: String, val displayName: String, val description: String) {
    CLOCK("CLOCK", "Minimalist Clock & Date", "Clean typography with battery and date info"),
    GLANCE("GLANCE", "Smart Glance Bar", "Contextual greeting, daily overview and weather glance"),
    SYSTEM_STATS("SYSTEM_STATS", "Device Performance Monitor", "RAM, Battery and Storage status with zero background drain"),
    QUICK_ACTIONS("QUICK_ACTIONS", "Quick Actions Strip", "Immediate shortcuts for Flashlight, Camera, Settings, and Tools"),
    QUICK_NOTE("QUICK_NOTE", "Minimal Scratchpad", "Direct home screen note for quick thoughts"),
    SEARCH_BAR("SEARCH_BAR", "Desktop Search Bar", "System search bar with math solver and web queries")
}

data class WidgetConfig(
    val type: WidgetType,
    val isEnabled: Boolean = true,
    val sortOrder: Int = 0,
    val styleType: String = "DEFAULT"
)
