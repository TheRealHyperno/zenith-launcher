package com.example.data.model

enum class GestureTrigger(val displayName: String, val description: String) {
    SWIPE_UP("Swipe Up", "Swipe up anywhere on desktop"),
    SWIPE_DOWN("Swipe Down", "Swipe down anywhere on desktop"),
    DOUBLE_TAP("Double Tap", "Double tap on empty desktop space"),
    PINCH_IN("Pinch In", "Pinch two fingers inward on desktop"),
    TWO_FINGER_SWIPE_UP("Two-Finger Swipe Up", "Swipe up using two fingers"),
    LONG_PRESS_DESKTOP("Long Press Desktop", "Hold down on empty home screen")
}

enum class GestureAction(val displayName: String, val iconName: String) {
    OPEN_DRAWER("Open App Drawer", "grid_view"),
    OPEN_SEARCH("Open Search", "search"),
    OPEN_NOTIFICATIONS("Open Notification Shade", "notifications"),
    EXPAND_QUICK_SETTINGS("Quick Settings Panel", "tune"),
    OPEN_SETTINGS("Launcher Settings", "settings"),
    LOCK_SCREEN("Lock Screen / Turn Off", "lock"),
    OPEN_HIDDEN_APPS("Open Hidden Apps", "visibility_off"),
    LAUNCH_FAVORITE_APP("Launch Selected App", "apps"),
    NONE("None (Do Nothing)", "block")
}

data class GestureBinding(
    val trigger: GestureTrigger,
    val action: GestureAction,
    val targetPackage: String? = null
)
