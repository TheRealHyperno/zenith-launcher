package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.data.model.SearchBarPosition
import com.example.data.model.WidgetType
import com.example.ui.components.AppIconView
import com.example.ui.components.SearchBarView
import com.example.ui.components.launcherDesktopGestures
import com.example.ui.widgets.AdaptiveClockWidget
import com.example.ui.widgets.GlancePillWidget
import com.example.ui.widgets.QuickActionsWidget
import com.example.ui.widgets.QuickNoteWidget
import com.example.ui.widgets.SystemStatsWidget
import com.example.viewmodel.LauncherScreen
import com.example.viewmodel.LauncherViewModel

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val pinnedApps by viewModel.pinnedHomeApps.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val widgets by viewModel.widgets.collectAsState()
    val quickNoteText by viewModel.quickNote.collectAsState()

    val isClockEnabled = widgets.any { it.type == WidgetType.CLOCK && it.isEnabled }
    val isGlanceEnabled = widgets.any { it.type == WidgetType.GLANCE && it.isEnabled }
    val isSystemStatsEnabled = widgets.any { it.type == WidgetType.SYSTEM_STATS && it.isEnabled }
    val isQuickActionsEnabled = widgets.any { it.type == WidgetType.QUICK_ACTIONS && it.isEnabled }
    val isQuickNoteEnabled = widgets.any { it.type == WidgetType.QUICK_NOTE && it.isEnabled }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(settings.themeStyle.backgroundColor)
            .launcherDesktopGestures { trigger ->
                viewModel.handleGestureTrigger(trigger, context)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Top Bar with settings quick shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(LauncherScreen.SETTINGS) },
                    modifier = Modifier.size(36.dp).testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Launcher Settings",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Top Search Bar if configured
            AnimatedVisibility(visible = settings.searchBarPosition == SearchBarPosition.TOP) {
                SearchBarView(
                    query = "",
                    onQueryChange = {
                        viewModel.onSearchQueryChange(it)
                        viewModel.navigateTo(LauncherScreen.DRAWER)
                    },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Scrollable widgets and desktop body
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Adaptive Clock Widget
                if (isClockEnabled) {
                    item(key = "widget_clock") {
                        AdaptiveClockWidget()
                    }
                }

                // Glance Pill Widget
                if (isGlanceEnabled) {
                    item(key = "widget_glance") {
                        GlancePillWidget()
                    }
                }

                // System Stats Widget
                if (isSystemStatsEnabled) {
                    item(key = "widget_system_stats") {
                        SystemStatsWidget()
                    }
                }

                // Quick Actions Widget
                if (isQuickActionsEnabled) {
                    item(key = "widget_quick_actions") {
                        QuickActionsWidget()
                    }
                }

                // Quick Note Widget
                if (isQuickNoteEnabled) {
                    item(key = "widget_quick_note") {
                        QuickNoteWidget(
                            noteContent = quickNoteText,
                            onNoteChange = { viewModel.saveQuickNote(it) }
                        )
                    }
                }

                // Desktop Pinned Apps Grid
                if (pinnedApps.isNotEmpty()) {
                    item(key = "pinned_apps_grid") {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = "Favorites",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                            )
                            DesktopGrid(
                                apps = pinnedApps,
                                columns = settings.gridColumns,
                                viewModel = viewModel
                            )
                        }
                    }
                }

                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Floating Bottom Search Bar if configured
            AnimatedVisibility(visible = settings.searchBarPosition == SearchBarPosition.BOTTOM) {
                SearchBarView(
                    query = "",
                    onQueryChange = {
                        viewModel.onSearchQueryChange(it)
                        viewModel.navigateTo(LauncherScreen.DRAWER)
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // Bottom Dock
            BottomDock(
                dockApps = dockApps,
                settings = settings,
                onAppClick = { viewModel.launchApp(it) },
                onAppLongClick = { viewModel.openAppMenu(it) },
                onOpenDrawer = { viewModel.navigateTo(LauncherScreen.DRAWER) }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DesktopGrid(
    apps: List<AppItem>,
    columns: Int,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()

    // Fixed height grid or wrapped row arrangement for LazyColumn compatibility
    val rows = apps.chunked(columns)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { rowApps ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowApps.forEach { app ->
                    AppIconView(
                        app = app,
                        iconShape = settings.iconShape,
                        iconThemeMode = settings.iconThemeMode,
                        showLabel = settings.showIconLabels,
                        iconScale = settings.iconScale,
                        badgeStyle = settings.badgeStyle,
                        badgeCount = app.badgeCount,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.launchApp(app) },
                        onLongClick = { viewModel.openAppMenu(app) }
                    )
                }
                // Fill remaining spaces in row
                val emptySlots = columns - rowApps.size
                if (emptySlots > 0) {
                    repeat(emptySlots) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun BottomDock(
    dockApps: List<AppItem>,
    settings: com.example.data.repository.LauncherSettingsState,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 12.dp)
        ) {
            // Swipe Up Handle
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onOpenDrawer)
                    .padding(horizontal = 24.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Open App Drawer",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Dock apps row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                dockApps.forEach { app ->
                    AppIconView(
                        app = app,
                        iconShape = settings.iconShape,
                        iconThemeMode = settings.iconThemeMode,
                        showLabel = false,
                        iconScale = settings.iconScale * 0.95f,
                        badgeStyle = settings.badgeStyle,
                        badgeCount = app.badgeCount,
                        modifier = Modifier.weight(1f),
                        onClick = { onAppClick(app) },
                        onLongClick = { onAppLongClick(app) }
                    )
                }
            }
        }
    }
}
