package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class IconShape(val displayName: String) {
    SQUIRCLE("Squircle"),
    CIRCLE("Circle"),
    ROUNDED_SQUARE("Rounded Square"),
    TEARDROP("Teardrop"),
    HEXAGON("Hexagon")
}

enum class IconThemeMode(val displayName: String, val description: String) {
    SYSTEM("System Default", "Native original app icons"),
    MONOCHROME("Monochrome Minimal", "Clean white outline on subtle dark background"),
    NEON("Cyber Neon", "Vibrant cyan & violet illuminated accents"),
    PASTEL("Pastel Dark", "Refined desaturated minimal palette"),
    EMERALD("Emerald Minimal", "Deep moss & golden highlight aesthetic")
}

enum class ThemeStyle(
    val displayName: String,
    val surfaceColor: Color,
    val backgroundColor: Color,
    val accentColor: Color,
    val textPrimary: Color,
    val textSecondary: Color
) {
    AMOLED_BLACK(
        displayName = "AMOLED Pure Black",
        surfaceColor = Color(0xFF101216),
        backgroundColor = Color(0xFF000000),
        accentColor = Color(0xFF00E5FF),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF8E95A5)
    ),
    MIDNIGHT_SLATE(
        displayName = "Midnight Slate",
        surfaceColor = Color(0xFF181C26),
        backgroundColor = Color(0xFF0D1017),
        accentColor = Color(0xFF60A5FA),
        textPrimary = Color(0xFFF1F5F9),
        textSecondary = Color(0xFF94A3B8)
    ),
    CYBER_OBSIDIAN(
        displayName = "Cyber Obsidian",
        surfaceColor = Color(0xFF161324),
        backgroundColor = Color(0xFF090710),
        accentColor = Color(0xFFA855F7),
        textPrimary = Color(0xFFFAF5FF),
        textSecondary = Color(0xFFA8A29E)
    ),
    FOREST_NIGHT(
        displayName = "Forest Night",
        surfaceColor = Color(0xFF121B16),
        backgroundColor = Color(0xFF070E0A),
        accentColor = Color(0xFF34D399),
        textPrimary = Color(0xFFECFDF5),
        textSecondary = Color(0xFF6EE7B7)
    )
}

enum class AppCategory(val displayName: String) {
    ALL("All"),
    FAVORITES("Favorites"),
    TOOLS("Tools & System"),
    SOCIAL("Social & Comms"),
    MEDIA("Media"),
    HIDDEN("Hidden")
}

enum class SearchBarPosition(val displayName: String) {
    BOTTOM("Bottom Floating"),
    TOP("Top Screen"),
    DOCK("Integrated in Dock"),
    HIDDEN("Hidden (Gesture Only)")
}

enum class BadgeStyle(val displayName: String) {
    DOT("Notification Dot"),
    NUMBER("Unread Count Number"),
    NONE("Disabled")
}
