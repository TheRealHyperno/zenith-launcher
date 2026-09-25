package com.example

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.service.AppWidgetHostManager
import com.example.ui.components.AppOptionSheet
import com.example.ui.screens.AppDrawerScreen
import com.example.ui.screens.HiddenAppsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ZenithTheme
import com.example.viewmodel.LauncherScreen
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.WidgetHostRequest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    private var pendingWidgetId: Int = -1
    private var pendingProviderInfo: AppWidgetProviderInfo? = null

    private val configureWidgetLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val widgetId = pendingWidgetId
        val provider = pendingProviderInfo
        pendingWidgetId = -1
        pendingProviderInfo = null

        if (result.resultCode == Activity.RESULT_OK && widgetId != -1 && provider != null) {
            viewModel.addAndroidAppWidget(widgetId, provider)
        } else if (widgetId != -1) {
            AppWidgetHostManager.deleteWidgetId(this, widgetId)
        }
    }

    private val bindWidgetLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val widgetId = pendingWidgetId
        val provider = pendingProviderInfo

        if (result.resultCode == Activity.RESULT_OK && widgetId != -1 && provider != null) {
            // Permission granted! Proceed to configure or add
            proceedToConfigureOrAdd(widgetId, provider)
        } else if (widgetId != -1) {
            AppWidgetHostManager.deleteWidgetId(this, widgetId)
            pendingWidgetId = -1
            pendingProviderInfo = null
        }
    }

    private val pickWidgetLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val widgetId = pendingWidgetId
        if (result.resultCode == Activity.RESULT_OK && widgetId != -1) {
            val returnedId = result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId) ?: widgetId
            val manager = AppWidgetHostManager.getManager(this)
            val info = manager.getAppWidgetInfo(returnedId)
            if (info != null) {
                proceedToConfigureOrAdd(returnedId, info)
            } else {
                AppWidgetHostManager.deleteWidgetId(this, widgetId)
            }
        } else if (widgetId != -1) {
            AppWidgetHostManager.deleteWidgetId(this, widgetId)
            pendingWidgetId = -1
            pendingProviderInfo = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            viewModel.widgetHostRequest.collect { request ->
                when (request) {
                    is WidgetHostRequest.PickProvider -> {
                        startAddAppWidgetFlow(request.providerInfo)
                    }
                    is WidgetHostRequest.OpenSystemPicker -> {
                        startSystemWidgetPicker()
                    }
                }
            }
        }

        setContent {
            val settings by viewModel.settings.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val selectedAppForMenu by viewModel.selectedAppForMenu.collectAsState()

            ZenithTheme(themeStyle = settings.themeStyle) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(settings.themeStyle.backgroundColor)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (targetState == LauncherScreen.DRAWER && initialState == LauncherScreen.HOME) {
                                slideInVertically { it } togetherWith slideOutVertically { -it / 3 }
                            } else if (targetState == LauncherScreen.HOME && initialState == LauncherScreen.DRAWER) {
                                slideInVertically { -it / 3 } togetherWith slideOutVertically { it }
                            } else if (targetState == LauncherScreen.SETTINGS || targetState == LauncherScreen.HIDDEN_APPS) {
                                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                            } else {
                                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                            }
                        },
                        label = "LauncherScreenTransition"
                    ) { screen ->
                        when (screen) {
                            LauncherScreen.HOME -> HomeScreen(viewModel = viewModel)
                            LauncherScreen.DRAWER -> AppDrawerScreen(viewModel = viewModel)
                            LauncherScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            LauncherScreen.HIDDEN_APPS -> HiddenAppsScreen(viewModel = viewModel)
                        }
                    }

                    // Long-press options bottom sheet
                    if (selectedAppForMenu != null) {
                        AppOptionSheet(
                            app = selectedAppForMenu,
                            iconShape = settings.iconShape,
                            iconThemeMode = settings.iconThemeMode,
                            onDismiss = { viewModel.closeAppMenu() },
                            onTogglePinHome = { viewModel.togglePinHome(it) },
                            onTogglePinDock = { viewModel.togglePinDock(it) },
                            onToggleHide = { viewModel.toggleHideApp(it) },
                            onRename = { app, newName -> viewModel.renameApp(app, newName) },
                            onAppInfo = { pkg -> viewModel.openAppInfo(pkg) },
                            onUninstall = { pkg -> viewModel.requestUninstall(pkg) }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AppWidgetHostManager.startListening(this)
    }

    override fun onStop() {
        super.onStop()
        AppWidgetHostManager.stopListening(this)
    }

    private fun startAddAppWidgetFlow(providerInfo: AppWidgetProviderInfo) {
        val host = AppWidgetHostManager.getHost(this)
        val manager = AppWidgetHostManager.getManager(this)
        val appWidgetId = host.allocateAppWidgetId()

        pendingWidgetId = appWidgetId
        pendingProviderInfo = providerInfo

        val canBind = try {
            manager.bindAppWidgetIdIfAllowed(appWidgetId, providerInfo.provider)
        } catch (_: Exception) {
            false
        }

        if (canBind) {
            proceedToConfigureOrAdd(appWidgetId, providerInfo)
        } else {
            // Request user confirmation to bind widget
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, providerInfo.provider)
            }
            try {
                bindWidgetLauncher.launch(intent)
            } catch (_: Exception) {
                // If bind intent fails, attempt direct fallback
                proceedToConfigureOrAdd(appWidgetId, providerInfo)
            }
        }
    }

    private fun proceedToConfigureOrAdd(widgetId: Int, providerInfo: AppWidgetProviderInfo) {
        if (providerInfo.configure != null) {
            pendingWidgetId = widgetId
            pendingProviderInfo = providerInfo
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                component = providerInfo.configure
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            try {
                configureWidgetLauncher.launch(intent)
            } catch (_: Exception) {
                // If configure activity fails, still add widget
                viewModel.addAndroidAppWidget(widgetId, providerInfo)
                pendingWidgetId = -1
                pendingProviderInfo = null
            }
        } else {
            viewModel.addAndroidAppWidget(widgetId, providerInfo)
            pendingWidgetId = -1
            pendingProviderInfo = null
        }
    }

    private fun startSystemWidgetPicker() {
        val host = AppWidgetHostManager.getHost(this)
        val appWidgetId = host.allocateAppWidgetId()
        pendingWidgetId = appWidgetId
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        try {
            pickWidgetLauncher.launch(intent)
        } catch (_: Exception) {
            host.deleteAppWidgetId(appWidgetId)
            pendingWidgetId = -1
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // If user presses Home hardware button, return to Home screen
        if (Intent.ACTION_MAIN == intent.action && intent.hasCategory(Intent.CATEGORY_HOME)) {
            viewModel.navigateTo(LauncherScreen.HOME)
        }
    }
}
