package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.ui.components.AppOptionSheet
import com.example.ui.screens.AppDrawerScreen
import com.example.ui.screens.HiddenAppsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ZenithTheme
import com.example.viewmodel.LauncherScreen
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // If user presses Home hardware button, return to Home screen
        if (Intent.ACTION_MAIN == intent.action && intent.hasCategory(Intent.CATEGORY_HOME)) {
            viewModel.navigateTo(LauncherScreen.HOME)
        }
    }
}
