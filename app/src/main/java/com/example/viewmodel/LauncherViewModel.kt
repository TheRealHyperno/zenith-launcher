package com.example.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.LauncherDatabase
import com.example.data.model.AppCategory
import com.example.data.model.AppItem
import com.example.data.model.BadgeStyle
import com.example.data.model.GestureAction
import com.example.data.model.GestureBinding
import com.example.data.model.GestureTrigger
import com.example.data.model.IconShape
import com.example.data.model.IconThemeMode
import com.example.data.model.SearchBarPosition
import com.example.data.model.ThemeStyle
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetSize
import com.example.data.model.WidgetType
import com.example.data.repository.AppRepository
import com.example.data.repository.GestureRepository
import com.example.data.repository.LauncherSettingsState
import com.example.data.repository.SettingsRepository
import com.example.data.repository.WidgetRepository
import com.example.service.NotificationBadgeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LauncherScreen {
    HOME,
    DRAWER,
    SETTINGS,
    HIDDEN_APPS
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LauncherDatabase.getDatabase(application)
    private val dao = db.launcherDao()

    val appRepository = AppRepository(application, dao)
    val gestureRepository = GestureRepository(dao)
    val settingsRepository = SettingsRepository(application)
    val widgetRepository = WidgetRepository(dao)

    private val _currentScreen = MutableStateFlow(LauncherScreen.HOME)
    val currentScreen: StateFlow<LauncherScreen> = _currentScreen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AppCategory.ALL)
    val selectedCategory: StateFlow<AppCategory> = _selectedCategory.asStateFlow()

    private val _selectedAppForMenu = MutableStateFlow<AppItem?>(null)
    val selectedAppForMenu: StateFlow<AppItem?> = _selectedAppForMenu.asStateFlow()

    // Desktop Widget Customization Mode
    private val _isWidgetEditMode = MutableStateFlow(false)
    val isWidgetEditMode: StateFlow<Boolean> = _isWidgetEditMode.asStateFlow()

    private val _showAddWidgetSheet = MutableStateFlow(false)
    val showAddWidgetSheet: StateFlow<Boolean> = _showAddWidgetSheet.asStateFlow()

    val settings: StateFlow<LauncherSettingsState> = settingsRepository.settings
    val gestures: StateFlow<Map<GestureTrigger, GestureBinding>> = gestureRepository.observeGestures()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val widgets: StateFlow<List<WidgetConfig>> = widgetRepository.observeWidgetInstances()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val quickNote: StateFlow<String> = widgetRepository.observeQuickNote()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val badgeCounts: StateFlow<Map<String, Int>> = NotificationBadgeManager.badgeCounts

    // All installed & merged apps with badge counts
    val allApps: StateFlow<List<AppItem>> = combine(
        appRepository.observeAllApps(),
        badgeCounts
    ) { apps, badges ->
        apps.map { app ->
            val count = badges[app.packageName] ?: 0
            if (app.badgeCount != count) app.copy(badgeCount = count) else app
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Desktop Pinned Apps
    val pinnedHomeApps: StateFlow<List<AppItem>> = allApps.combine(settings) { apps, _ ->
        apps.filter { it.isPinnedHome && !it.isHidden }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Dock Apps
    val dockApps: StateFlow<List<AppItem>> = allApps.combine(settings) { apps, setts ->
        apps.filter { it.isPinnedDock && !it.isHidden }
            .sortedBy { it.dockIndex }
            .take(setts.dockCount)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Hidden Apps
    val hiddenApps: StateFlow<List<AppItem>> = allApps.combine(settings) { apps, _ ->
        apps.filter { it.isHidden }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // App Drawer Apps
    val drawerApps: StateFlow<List<AppItem>> = combine(
        allApps,
        _searchQuery,
        _selectedCategory
    ) { apps, query, category ->
        val visible = apps.filter { !it.isHidden }
        val categoryFiltered = when (category) {
            AppCategory.ALL -> visible
            AppCategory.FAVORITES -> visible.filter { it.isPinnedHome || it.launchCount > 0 }
            AppCategory.HIDDEN -> apps.filter { it.isHidden }
            else -> visible.filter { it.category == category }
        }

        if (query.isBlank()) {
            categoryFiltered
        } else {
            val q = query.lowercase().trim()
            visible.filter {
                it.displayName.lowercase().contains(q) ||
                it.packageName.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun navigateTo(screen: LauncherScreen) {
        _currentScreen.value = screen
        if (screen == LauncherScreen.HOME) {
            _searchQuery.value = ""
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: AppCategory) {
        _selectedCategory.value = category
    }

    fun openAppMenu(app: AppItem) {
        _selectedAppForMenu.value = app
    }

    fun closeAppMenu() {
        _selectedAppForMenu.value = null
    }

    fun launchApp(app: AppItem) {
        viewModelScope.launch {
            appRepository.recordLaunch(app.packageName)
        }
        appRepository.launchApp(app)
        if (_currentScreen.value == LauncherScreen.DRAWER) {
            _currentScreen.value = LauncherScreen.HOME
            _searchQuery.value = ""
        }
    }

    fun togglePinHome(app: AppItem) {
        viewModelScope.launch {
            appRepository.togglePinToHome(app)
        }
    }

    fun togglePinDock(app: AppItem) {
        viewModelScope.launch {
            appRepository.togglePinToDock(app, dockApps.value.size)
        }
    }

    fun toggleHideApp(app: AppItem) {
        viewModelScope.launch {
            appRepository.toggleHideApp(app)
        }
    }

    fun renameApp(app: AppItem, newName: String) {
        viewModelScope.launch {
            appRepository.setCustomLabel(app.packageName, newName)
        }
    }

    fun openAppInfo(packageName: String) {
        appRepository.openAppInfo(packageName)
    }

    fun requestUninstall(packageName: String) {
        appRepository.requestUninstall(packageName)
    }

    fun saveQuickNote(text: String) {
        viewModelScope.launch {
            widgetRepository.saveQuickNote(text)
        }
    }

    // Widget Customizer Controls
    fun toggleWidgetEditMode() {
        _isWidgetEditMode.value = !_isWidgetEditMode.value
    }

    fun openAddWidgetSheet() {
        _showAddWidgetSheet.value = true
    }

    fun closeAddWidgetSheet() {
        _showAddWidgetSheet.value = false
    }

    fun addNewWidget(type: WidgetType) {
        viewModelScope.launch {
            widgetRepository.addWidget(type, widgets.value.size)
            _isWidgetEditMode.value = true
        }
    }

    fun updateWidgetSize(id: String, newSize: WidgetSize) {
        viewModelScope.launch {
            widgetRepository.updateWidgetSize(id, newSize)
        }
    }

    fun removeWidget(id: String) {
        viewModelScope.launch {
            widgetRepository.removeWidget(id)
        }
    }

    fun moveWidget(id: String, direction: Int) {
        viewModelScope.launch {
            widgetRepository.moveWidget(id, direction, widgets.value)
        }
    }

    fun updateWidgetConfig(id: String, title: String, customData: String) {
        viewModelScope.launch {
            widgetRepository.updateWidgetCustomData(id, title, customData)
        }
    }

    // Gesture Execution
    fun handleGestureTrigger(trigger: GestureTrigger, context: Context) {
        val binding = gestures.value[trigger] ?: return
        executeGestureAction(binding.action, binding.targetPackage, context)
    }

    @SuppressLint("WrongConstant")
    fun executeGestureAction(action: GestureAction, targetPackage: String?, context: Context) {
        when (action) {
            GestureAction.OPEN_DRAWER -> {
                navigateTo(LauncherScreen.DRAWER)
            }
            GestureAction.OPEN_SEARCH -> {
                navigateTo(LauncherScreen.DRAWER)
            }
            GestureAction.OPEN_NOTIFICATIONS -> {
                expandNotificationShade(context)
            }
            GestureAction.EXPAND_QUICK_SETTINGS -> {
                expandQuickSettings(context)
            }
            GestureAction.OPEN_SETTINGS -> {
                navigateTo(LauncherScreen.SETTINGS)
            }
            GestureAction.OPEN_HIDDEN_APPS -> {
                navigateTo(LauncherScreen.HIDDEN_APPS)
            }
            GestureAction.LAUNCH_FAVORITE_APP -> {
                if (!targetPackage.isNullOrBlank()) {
                    val app = allApps.value.find { it.packageName == targetPackage }
                    if (app != null) launchApp(app)
                }
            }
            GestureAction.LOCK_SCREEN -> {
                openDeviceLockOrDisplaySettings(context)
            }
            GestureAction.NONE -> {}
        }
    }

    private fun expandNotificationShade(context: Context) {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManagerClass = Class.forName("android.app.StatusBarManager")
            val method = statusBarManagerClass.getMethod("expandNotificationsPanel")
            method.invoke(statusBarService)
        } catch (_: Exception) {}
    }

    private fun expandQuickSettings(context: Context) {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManagerClass = Class.forName("android.app.StatusBarManager")
            val method = statusBarManagerClass.getMethod("expandSettingsPanel")
            method.invoke(statusBarService)
        } catch (_: Exception) {
            expandNotificationShade(context)
        }
    }

    private fun openDeviceLockOrDisplaySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    // Settings Updates
    fun updateGridColumns(cols: Int) = settingsRepository.updateGridColumns(cols)
    fun updateDockCount(count: Int) = settingsRepository.updateDockCount(count)
    fun updateIconScale(scale: Float) = settingsRepository.updateIconScale(scale)
    fun updateShowIconLabels(show: Boolean) = settingsRepository.updateShowIconLabels(show)
    fun updateIconShape(shape: IconShape) = settingsRepository.updateIconShape(shape)
    fun updateIconThemeMode(mode: IconThemeMode) = settingsRepository.updateIconThemeMode(mode)
    fun updateThemeStyle(style: ThemeStyle) = settingsRepository.updateThemeStyle(style)
    fun updateAnimationSpeed(speed: Float) = settingsRepository.updateAnimationSpeed(speed)
    fun updateSearchBarPosition(pos: SearchBarPosition) = settingsRepository.updateSearchBarPosition(pos)
    fun updateBadgeStyle(style: BadgeStyle) = settingsRepository.updateBadgeStyle(style)
    fun updateHapticFeedback(enabled: Boolean) = settingsRepository.updateHapticFeedback(enabled)
    fun toggleBatterySaver(enabled: Boolean) = settingsRepository.toggleBatterySaverMode(enabled)
    fun updateWidgetCornerRadius(radius: Int) = settingsRepository.updateWidgetCornerRadius(radius)
    fun updateWidgetOpacity(opacity: Float) = settingsRepository.updateWidgetOpacity(opacity)

    fun updateGestureBinding(trigger: GestureTrigger, action: GestureAction, targetPackage: String? = null) {
        viewModelScope.launch {
            gestureRepository.updateGesture(trigger, action, targetPackage)
        }
    }
}
