package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.ThemeStyle

@Composable
fun ZenithTheme(
    themeStyle: ThemeStyle = ThemeStyle.AMOLED_BLACK,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = themeStyle.accentColor,
        onPrimary = Color.Black,
        primaryContainer = themeStyle.accentColor.copy(alpha = 0.18f),
        onPrimaryContainer = themeStyle.accentColor,
        secondary = themeStyle.accentColor.copy(alpha = 0.85f),
        onSecondary = Color.Black,
        secondaryContainer = themeStyle.surfaceColor,
        onSecondaryContainer = themeStyle.textPrimary,
        tertiary = Color(0xFFA78BFA),
        background = themeStyle.backgroundColor,
        onBackground = themeStyle.textPrimary,
        surface = themeStyle.surfaceColor,
        onSurface = themeStyle.textPrimary,
        surfaceVariant = themeStyle.surfaceColor.copy(alpha = 0.92f),
        onSurfaceVariant = themeStyle.textSecondary,
        outline = Color(0xFF334155),
        outlineVariant = Color(0xFF1E293B)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ZenithTheme(
        themeStyle = ThemeStyle.AMOLED_BLACK,
        content = content
    )
}
