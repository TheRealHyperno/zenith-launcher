package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.data.model.SearchBarPosition
import com.example.data.model.WidgetType
import com.example.ui.components.AppIconView
import com.example.ui.components.SearchBarView
import com.example.ui.components.launcherDesktopGestures
import com.example.ui.widgets.AdaptiveClockWidget
import com.example.ui.widgets.AdaptiveFitnessWidget
import com.example.ui.widgets.AdaptiveMusicWidget
import com.example.ui.widgets.AdaptiveScreenTimeWidget
import com.example.ui.widgets.AdaptiveWorldClockWidget
import com.example.ui.widgets.AddWidgetSheet
import com.example.ui.widgets.AndroidAppWidgetHostView
import com.example.ui.widgets.AppClusterWidget
import com.example.ui.widgets.CountdownWidget
import com.example.ui.widgets.CustomQuoteWidget
import com.example.ui.widgets.GlancePillWidget
import com.example.ui.widgets.HabitTrackerWidget
import com.example.ui.widgets.QuickActionsWidget
import com.example.ui.widgets.QuickNoteWidget
import com.example.ui.widgets.SystemStatsWidget
import com.example.ui.widgets.WebShortcutWidget
import com.example.ui.widgets.WidgetContainer
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
    val allApps by viewModel.allApps.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val widgets by viewModel.widgets.collectAsState()
    val quickNoteText by viewModel.quickNote.collectAsState()
    val isEditMode by viewModel.isWidgetEditMode.collectAsState()
    val showAddSheet by viewModel.showAddWidgetSheet.collectAsState()
    val installedAppWidgets by viewModel.installedAppWidgets.collectAsState()

    LaunchedEffect(showAddSheet) {
        if (showAddSheet) {
            viewModel.loadInstalledAppWidgets()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(settings.themeStyle.backgroundColor)
            .launcherDesktopGestures { trigger ->
                if (!isEditMode) {
                    viewModel.handleGestureTrigger(trigger, context)
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Top Bar with Widget Edit Toggle & Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Widget Customizer pill button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isEditMode) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    modifier = Modifier.clickable { viewModel.toggleWidgetEditMode() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Tune,
                            contentDescription = "Customize Widgets",
                            tint = if (isEditMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEditMode) "Done Editing" else "Customize",
                            fontSize = 12.sp,
                            fontWeight = if (isEditMode) FontWeight.Bold else FontWeight.Normal,
                            color = if (isEditMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isEditMode) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { viewModel.openAddWidgetSheet() }
                                .padding(end = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Widget",
                                    tint = Color.Black,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add Widget",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }

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
                // Dynamic Adaptive & Custom Widgets
                items(
                    items = widgets.filter { it.isEnabled },
                    key = { it.id }
                ) { widget ->
                    WidgetContainer(
                        widget = widget,
                        isEditMode = isEditMode,
                        cornerRadiusDp = settings.widgetCornerRadius,
                        opacity = settings.widgetOpacity,
                        isPerformanceMode = settings.isPerformanceMode,
                        onResize = { newSize -> viewModel.updateWidgetSize(widget.id, newSize) },
                        onMoveUp = { viewModel.moveWidget(widget.id, -1) },
                        onMoveDown = { viewModel.moveWidget(widget.id, 1) },
                        onDelete = { viewModel.removeWidget(widget.id) },
                        onToggleEditMode = { viewModel.toggleWidgetEditMode() }
                    ) { currentSize ->
                        when (widget.type) {
                            WidgetType.CLOCK -> {
                                AdaptiveClockWidget(size = currentSize)
                            }
                            WidgetType.GLANCE -> {
                                GlancePillWidget(size = currentSize)
                            }
                            WidgetType.SYSTEM_STATS -> {
                                SystemStatsWidget(size = currentSize)
                            }
                            WidgetType.QUICK_ACTIONS -> {
                                QuickActionsWidget(size = currentSize)
                            }
                            WidgetType.QUICK_NOTE -> {
                                QuickNoteWidget(
                                    noteContent = quickNoteText,
                                    onNoteChange = { viewModel.saveQuickNote(it) },
                                    size = currentSize
                                )
                            }
                            WidgetType.COUNTDOWN -> {
                                CountdownWidget(
                                    title = widget.title,
                                    customData = widget.customData,
                                    size = currentSize,
                                    onUpdateConfig = { title, data ->
                                        viewModel.updateWidgetConfig(widget.id, title, data)
                                    }
                                )
                            }
                            WidgetType.HABIT_TRACKER -> {
                                HabitTrackerWidget(
                                    title = widget.title,
                                    customData = widget.customData,
                                    size = currentSize,
                                    onUpdateConfig = { title, data ->
                                        viewModel.updateWidgetConfig(widget.id, title, data)
                                    }
                                )
                            }
                            WidgetType.APP_CLUSTER -> {
                                AppClusterWidget(
                                    title = widget.title,
                                    allApps = allApps,
                                    iconShape = settings.iconShape,
                                    iconThemeMode = settings.iconThemeMode,
                                    size = currentSize,
                                    onAppClick = { viewModel.launchApp(it) }
                                )
                            }
                            WidgetType.CUSTOM_QUOTE -> {
                                CustomQuoteWidget(
                                    title = widget.title,
                                    customData = widget.customData,
                                    size = currentSize,
                                    onUpdateConfig = { title, data ->
                                        viewModel.updateWidgetConfig(widget.id, title, data)
                                    }
                                )
                            }
                            WidgetType.WEB_SHORTCUT -> {
                                WebShortcutWidget(size = currentSize)
                            }
                            WidgetType.MUSIC_PLAYER -> {
                                AdaptiveMusicWidget(size = currentSize)
                            }
                            WidgetType.WORLD_CLOCK -> {
                                AdaptiveWorldClockWidget(size = currentSize)
                            }
                            WidgetType.FITNESS_STEPS -> {
                                AdaptiveFitnessWidget(size = currentSize)
                            }
                            WidgetType.SCREEN_TIME -> {
                                AdaptiveScreenTimeWidget(size = currentSize)
                            }
                            WidgetType.ANDROID_APPWIDGET -> {
                                val parts = widget.customData.split("|")
                                val appWidgetId = parts.firstOrNull()?.toIntOrNull() ?: -1
                                val comp = parts.getOrNull(1) ?: ""
                                if (appWidgetId != -1) {
                                    AndroidAppWidgetHostView(
                                        appWidgetId = appWidgetId,
                                        componentNameString = comp,
                                        size = currentSize,
                                        isPerformanceMode = settings.isPerformanceMode
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }

                // Add Widget Button when in Edit Mode or if widgets list is empty
                if (isEditMode || widgets.none { it.isEnabled }) {
                    item(key = "add_widget_card") {
                        Surface(
                            shape = RoundedCornerShape(settings.widgetCornerRadius.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openAddWidgetSheet() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add Another Widget",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
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

        // Add Widget Catalog Bottom Sheet
        if (showAddSheet) {
            AddWidgetSheet(
                installedAppWidgets = installedAppWidgets,
                onDismiss = { viewModel.closeAddWidgetSheet() },
                onAddWidget = { type -> viewModel.addNewWidget(type) },
                onSelectAppWidget = { providerInfo ->
                    viewModel.requestAddSystemWidget(providerInfo)
                },
                onOpenSystemPicker = {
                    viewModel.requestOpenSystemWidgetPicker()
                }
            )
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
