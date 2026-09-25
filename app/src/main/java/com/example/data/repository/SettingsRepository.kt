package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.BadgeStyle
import com.example.data.model.IconShape
import com.example.data.model.IconThemeMode
import com.example.data.model.SearchBarPosition
import com.example.data.model.ThemeStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LauncherSettingsState(
    val gridColumns: Int = 4,
    val dockCount: Int = 4,
    val iconScale: Float = 1.0f,
    val showIconLabels: Boolean = true,
    val iconShape: IconShape = IconShape.SQUIRCLE,
    val iconThemeMode: IconThemeMode = IconThemeMode.SYSTEM,
    val themeStyle: ThemeStyle = ThemeStyle.AMOLED_BLACK,
    val animationSpeed: Float = 1.0f,
    val searchBarPosition: SearchBarPosition = SearchBarPosition.BOTTOM,
    val badgeStyle: BadgeStyle = BadgeStyle.DOT,
    val hapticFeedback: Boolean = true,
    val batterySaverMode: Boolean = false
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("zenith_launcher_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(readFromPrefs())
    val settings: StateFlow<LauncherSettingsState> = _settings.asStateFlow()

    private fun readFromPrefs(): LauncherSettingsState {
        val shapeName = prefs.getString("icon_shape", IconShape.SQUIRCLE.name) ?: IconShape.SQUIRCLE.name
        val themeModeName = prefs.getString("icon_theme", IconThemeMode.SYSTEM.name) ?: IconThemeMode.SYSTEM.name
        val themeStyleName = prefs.getString("theme_style", ThemeStyle.AMOLED_BLACK.name) ?: ThemeStyle.AMOLED_BLACK.name
        val searchPosName = prefs.getString("search_pos", SearchBarPosition.BOTTOM.name) ?: SearchBarPosition.BOTTOM.name
        val badgeStyleName = prefs.getString("badge_style", BadgeStyle.DOT.name) ?: BadgeStyle.DOT.name

        return LauncherSettingsState(
            gridColumns = prefs.getInt("grid_columns", 4),
            dockCount = prefs.getInt("dock_count", 4),
            iconScale = prefs.getFloat("icon_scale", 1.0f),
            showIconLabels = prefs.getBoolean("show_icon_labels", true),
            iconShape = try { IconShape.valueOf(shapeName) } catch (_: Exception) { IconShape.SQUIRCLE },
            iconThemeMode = try { IconThemeMode.valueOf(themeModeName) } catch (_: Exception) { IconThemeMode.SYSTEM },
            themeStyle = try { ThemeStyle.valueOf(themeStyleName) } catch (_: Exception) { ThemeStyle.AMOLED_BLACK },
            animationSpeed = prefs.getFloat("anim_speed", 1.0f),
            searchBarPosition = try { SearchBarPosition.valueOf(searchPosName) } catch (_: Exception) { SearchBarPosition.BOTTOM },
            badgeStyle = try { BadgeStyle.valueOf(badgeStyleName) } catch (_: Exception) { BadgeStyle.DOT },
            hapticFeedback = prefs.getBoolean("haptic_feedback", true),
            batterySaverMode = prefs.getBoolean("battery_saver_mode", false)
        )
    }

    fun updateGridColumns(columns: Int) {
        prefs.edit().putInt("grid_columns", columns).apply()
        _settings.value = _settings.value.copy(gridColumns = columns)
    }

    fun updateDockCount(count: Int) {
        prefs.edit().putInt("dock_count", count).apply()
        _settings.value = _settings.value.copy(dockCount = count)
    }

    fun updateIconScale(scale: Float) {
        prefs.edit().putFloat("icon_scale", scale).apply()
        _settings.value = _settings.value.copy(iconScale = scale)
    }

    fun updateShowIconLabels(show: Boolean) {
        prefs.edit().putBoolean("show_icon_labels", show).apply()
        _settings.value = _settings.value.copy(showIconLabels = show)
    }

    fun updateIconShape(shape: IconShape) {
        prefs.edit().putString("icon_shape", shape.name).apply()
        _settings.value = _settings.value.copy(iconShape = shape)
    }

    fun updateIconThemeMode(mode: IconThemeMode) {
        prefs.edit().putString("icon_theme", mode.name).apply()
        _settings.value = _settings.value.copy(iconThemeMode = mode)
    }

    fun updateThemeStyle(style: ThemeStyle) {
        prefs.edit().putString("theme_style", style.name).apply()
        _settings.value = _settings.value.copy(themeStyle = style)
    }

    fun updateAnimationSpeed(speed: Float) {
        prefs.edit().putFloat("anim_speed", speed).apply()
        _settings.value = _settings.value.copy(animationSpeed = speed)
    }

    fun updateSearchBarPosition(position: SearchBarPosition) {
        prefs.edit().putString("search_pos", position.name).apply()
        _settings.value = _settings.value.copy(searchBarPosition = position)
    }

    fun updateBadgeStyle(style: BadgeStyle) {
        prefs.edit().putString("badge_style", style.name).apply()
        _settings.value = _settings.value.copy(badgeStyle = style)
    }

    fun updateHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean("haptic_feedback", enabled).apply()
        _settings.value = _settings.value.copy(hapticFeedback = enabled)
    }

    fun toggleBatterySaverMode(enabled: Boolean) {
        val animSpeed = if (enabled) 0.0f else 1.0f
        prefs.edit()
            .putBoolean("battery_saver_mode", enabled)
            .putFloat("anim_speed", animSpeed)
            .apply()
        _settings.value = _settings.value.copy(
            batterySaverMode = enabled,
            animationSpeed = animSpeed
        )
    }
}
