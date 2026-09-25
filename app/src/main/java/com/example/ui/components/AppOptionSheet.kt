package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.data.model.BadgeStyle
import com.example.data.model.IconShape
import com.example.data.model.IconThemeMode
import com.example.service.NotificationBadgeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOptionSheet(
    app: AppItem?,
    iconShape: IconShape,
    iconThemeMode: IconThemeMode,
    onDismiss: () -> Unit,
    onTogglePinHome: (AppItem) -> Unit,
    onTogglePinDock: (AppItem) -> Unit,
    onToggleHide: (AppItem) -> Unit,
    onRename: (AppItem, String) -> Unit,
    onAppInfo: (String) -> Unit,
    onUninstall: (String) -> Unit
) {
    if (app == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameText by remember(app) { mutableStateOf(app.displayName) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppIconView(
                    app = app,
                    iconShape = iconShape,
                    iconThemeMode = iconThemeMode,
                    showLabel = false,
                    iconScale = 1.0f,
                    badgeStyle = BadgeStyle.NONE,
                    badgeCount = 0,
                    onClick = {},
                    onLongClick = {}
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.displayName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = app.packageName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Options List
            AppOptionRow(
                icon = Icons.Default.PushPin,
                title = if (app.isPinnedHome) "Remove from Home Screen" else "Pin to Home Screen",
                onClick = {
                    onTogglePinHome(app)
                    onDismiss()
                }
            )

            AppOptionRow(
                icon = Icons.Default.VerticalAlignBottom,
                title = if (app.isPinnedDock) "Remove from Dock" else "Pin to Dock",
                onClick = {
                    onTogglePinDock(app)
                    onDismiss()
                }
            )

            AppOptionRow(
                icon = Icons.Default.Edit,
                title = "Edit App Label",
                onClick = {
                    showRenameDialog = true
                }
            )

            AppOptionRow(
                icon = Icons.Default.VisibilityOff,
                title = if (app.isHidden) "Unhide from Drawer" else "Hide from App Drawer",
                onClick = {
                    onToggleHide(app)
                    onDismiss()
                }
            )

            AppOptionRow(
                icon = Icons.Default.Notifications,
                title = "Test Badge (${if (app.badgeCount > 0) "Clear" else "+3"})",
                onClick = {
                    if (app.badgeCount > 0) {
                        NotificationBadgeManager.clearBadge(app.packageName)
                    } else {
                        NotificationBadgeManager.simulateTestBadge(app.packageName, 3)
                    }
                    onDismiss()
                }
            )

            AppOptionRow(
                icon = Icons.Default.Info,
                title = "App Info",
                onClick = {
                    onAppInfo(app.packageName)
                    onDismiss()
                }
            )

            AppOptionRow(
                icon = Icons.Default.Delete,
                title = "Uninstall App",
                tint = Color(0xFFEF4444),
                onClick = {
                    onUninstall(app.packageName)
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename ${app.label}") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Custom App Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRename(app, renameText.trim())
                        showRenameDialog = false
                        onDismiss()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AppOptionRow(
    icon: ImageVector,
    title: String,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            color = tint
        )
    }
}
