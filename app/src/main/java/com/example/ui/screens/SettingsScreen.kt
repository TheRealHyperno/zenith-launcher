package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeStyle
import com.example.data.model.GestureAction
import com.example.data.model.GestureTrigger
import com.example.data.model.IconShape
import com.example.data.model.IconThemeMode
import com.example.data.model.SearchBarPosition
import com.example.data.model.ThemeStyle
import com.example.data.model.WidgetType
import com.example.service.NotificationBadgeManager
import com.example.viewmodel.LauncherScreen
import com.example.viewmodel.LauncherViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val gestures by viewModel.gestures.collectAsState()
    val widgets by viewModel.widgets.collectAsState()
    val isNotificationServiceConnected by NotificationBadgeManager.isServiceConnected.collectAsState()

    var selectedGestureTriggerToEdit by remember { mutableStateOf<GestureTrigger?>(null) }

    BackHandler {
        viewModel.navigateTo(LauncherScreen.HOME)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Zenith Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(LauncherScreen.HOME) },
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = settings.themeStyle.backgroundColor,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = settings.themeStyle.backgroundColor,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Set as Default Launcher Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Default Launcher",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Set Zenith as your primary home app for instant access.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { openDefaultLauncherSettings(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Set", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // SECTION: Look & Feel (Themes & Icons)
            item {
                SettingsSection(
                    title = "Look & Feel",
                    icon = Icons.Default.ColorLens
                ) {
                    // Theme Style
                    Text(
                        text = "Theme Palette",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeStyle.entries.forEach { style ->
                            FilterChip(
                                selected = settings.themeStyle == style,
                                onClick = { viewModel.updateThemeStyle(style) },
                                label = { Text(style.displayName, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Icon Shape
                    Text(
                        text = "Icon Shape",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconShape.entries.forEach { shape ->
                            FilterChip(
                                selected = settings.iconShape == shape,
                                onClick = { viewModel.updateIconShape(shape) },
                                label = { Text(shape.displayName, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Icon Theme / Pack
                    Text(
                        text = "Icon Pack Style",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconThemeMode.entries.forEach { mode ->
                            FilterChip(
                                selected = settings.iconThemeMode == mode,
                                onClick = { viewModel.updateIconThemeMode(mode) },
                                label = { Text(mode.displayName, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // SECTION: Home Screen Layout & Grid
            item {
                SettingsSection(
                    title = "Desktop & Grid Layout",
                    icon = Icons.Default.GridView
                ) {
                    // Columns
                    Text(
                        text = "Grid Columns",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(3, 4, 5, 6).forEach { cols ->
                            FilterChip(
                                selected = settings.gridColumns == cols,
                                onClick = { viewModel.updateGridColumns(cols) },
                                label = { Text("$cols Columns", fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dock Count
                    Text(
                        text = "Dock Apps Count",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(4, 5).forEach { count ->
                            FilterChip(
                                selected = settings.dockCount == count,
                                onClick = { viewModel.updateDockCount(count) },
                                label = { Text("$count Apps", fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search Bar Position
                    Text(
                        text = "Search Bar Position",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SearchBarPosition.entries.forEach { pos ->
                            FilterChip(
                                selected = settings.searchBarPosition == pos,
                                onClick = { viewModel.updateSearchBarPosition(pos) },
                                label = { Text(pos.displayName, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Show App Labels Toggle
                    SettingsSwitchRow(
                        title = "Show App Labels",
                        subtitle = "Display text label under app icons",
                        checked = settings.showIconLabels,
                        onCheckedChange = { viewModel.updateShowIconLabels(it) }
                    )
                }
            }

            // SECTION: Customizable Gesture Controls
            item {
                SettingsSection(
                    title = "Customizable Gestures",
                    icon = Icons.Default.Gesture
                ) {
                    Text(
                        text = "Configure desktop gesture triggers and actions",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    GestureTrigger.entries.forEach { trigger ->
                        val binding = gestures[trigger]
                        val actionName = binding?.action?.displayName ?: "None"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedGestureTriggerToEdit = trigger }
                                .padding(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = trigger.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = trigger.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            Text(
                                text = actionName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            // SECTION: Notification Badges & System Access
            item {
                SettingsSection(
                    title = "Notification Badges",
                    icon = Icons.Default.Notifications
                ) {
                    // Badge style
                    Text(
                        text = "Badge Style",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BadgeStyle.entries.forEach { style ->
                            FilterChip(
                                selected = settings.badgeStyle == style,
                                onClick = { viewModel.updateBadgeStyle(style) },
                                label = { Text(style.displayName, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // System Listener Permission Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "System Notification Access",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isNotificationServiceConnected) "Service active & counting unread alerts" else "Permission needed for live system badges",
                                fontSize = 11.sp,
                                color = if (isNotificationServiceConnected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        Button(
                            onClick = { openNotificationListenerSettings(context) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isNotificationServiceConnected) "Manage" else "Grant")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Test Badge Button
                    Button(
                        onClick = {
                            NotificationBadgeManager.simulateTestBadge("com.google.android.apps.messaging", 5)
                            NotificationBadgeManager.simulateTestBadge("com.google.android.dialer", 2)
                            Toast.makeText(context, "Test notification badges activated!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate Test Badges on Sample Apps")
                    }
                }
            }

            // SECTION: Adaptive Desktop Widgets & Customization
            item {
                SettingsSection(
                    title = "Desktop Widgets & Styling",
                    icon = Icons.Default.Widgets
                ) {
                    Text(
                        text = "Customize corner radius, transparency, and manage home screen widgets",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    // Widget Corner Radius
                    Text(
                        text = "Widget Corner Radius",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        listOf(8, 16, 20, 28).forEach { radius ->
                            FilterChip(
                                selected = settings.widgetCornerRadius == radius,
                                onClick = { viewModel.updateWidgetCornerRadius(radius) },
                                label = { Text("${radius}dp", fontSize = 12.sp) }
                            )
                        }
                    }

                    // Widget Background Opacity
                    Text(
                        text = "Widget Background Opacity",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        listOf(0.40f to "40%", 0.65f to "65%", 0.75f to "75%", 0.95f to "95%").forEach { (opacityVal, label) ->
                            FilterChip(
                                selected = kotlin.math.abs(settings.widgetOpacity - opacityVal) < 0.05f,
                                onClick = { viewModel.updateWidgetOpacity(opacityVal) },
                                label = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.navigateTo(LauncherScreen.HOME)
                            viewModel.toggleWidgetEditMode()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Customize & Resize Widgets on Home Screen", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // SECTION: Performance & Battery Optimization
            item {
                SettingsSection(
                    title = "Performance & Battery",
                    icon = Icons.Default.BatteryChargingFull
                ) {
                    Text(
                        text = "Zenith uses zero background wake loops and caches icons in memory to ensure maximum smoothness on all devices.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    SettingsSwitchRow(
                        title = "Performance Mode / Battery Saver",
                        subtitle = "Disables fluid widget sizing animations and motion transitions for instantaneous static rendering and maximum battery life",
                        checked = settings.batterySaverMode,
                        onCheckedChange = { viewModel.toggleBatterySaver(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsSwitchRow(
                        title = "Haptic Feedback",
                        subtitle = "Subtle vibration on app drawer A-Z scrubber and drag",
                        checked = settings.hapticFeedback,
                        onCheckedChange = { viewModel.updateHapticFeedback(it) }
                    )
                }
            }

            // SECTION: Hidden Apps
            item {
                SettingsSection(
                    title = "Privacy & Hidden Apps",
                    icon = Icons.Default.Security
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.navigateTo(LauncherScreen.HIDDEN_APPS) }
                            .padding(vertical = 10.dp, horizontal = 6.dp)
                    ) {
                        Column {
                            Text(
                                text = "Manage Hidden Apps",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "View and unhide apps hidden from the drawer",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        Text(
                            text = "Open",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Gesture Action Picker Dialog
    if (selectedGestureTriggerToEdit != null) {
        val trigger = selectedGestureTriggerToEdit!!
        val currentBinding = gestures[trigger]

        AlertDialog(
            onDismissRequest = { selectedGestureTriggerToEdit = null },
            title = { Text("Action for ${trigger.displayName}") },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(GestureAction.entries.size) { index ->
                        val action = GestureAction.entries[index]
                        val isSelected = currentBinding?.action == action
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable {
                                    viewModel.updateGestureBinding(trigger, action)
                                    selectedGestureTriggerToEdit = null
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = action.displayName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedGestureTriggerToEdit = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

fun openDefaultLauncherSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val fallback = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        } catch (_: Exception) {
            Toast.makeText(context, "Select Zenith Launcher in Android Settings > Apps > Default Apps", Toast.LENGTH_LONG).show()
        }
    }
}

fun openNotificationListenerSettings(context: Context) {
    try {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Open Settings > Notification Access", Toast.LENGTH_SHORT).show()
    }
}
