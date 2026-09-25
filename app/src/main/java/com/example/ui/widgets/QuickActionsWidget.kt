package com.example.ui.widgets

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WidgetSize
import com.example.ui.components.openCalculator

@Composable
fun QuickActionsWidget(
    size: WidgetSize = WidgetSize.STANDARD,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isTorchOn by remember { mutableStateOf(false) }

    when (size) {
        WidgetSize.COMPACT -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickActionButton(
                    icon = if (isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                    label = "Torch",
                    isActive = isTorchOn,
                    activeColor = Color(0xFFF59E0B),
                    isCompact = true,
                    onClick = { isTorchOn = toggleTorch(context, !isTorchOn) }
                )

                QuickActionButton(
                    icon = Icons.Default.PhotoCamera,
                    label = "Camera",
                    isCompact = true,
                    onClick = { openCamera(context) }
                )

                QuickActionButton(
                    icon = Icons.Default.Calculate,
                    label = "Calc",
                    isCompact = true,
                    onClick = { openCalculator(context) }
                )

                QuickActionButton(
                    icon = Icons.Default.Wifi,
                    label = "Wi-Fi",
                    isCompact = true,
                    onClick = { openWifiSettings(context) }
                )
            }
        }
        WidgetSize.STANDARD -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickActionButton(
                    icon = if (isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                    label = if (isTorchOn) "Torch On" else "Torch",
                    isActive = isTorchOn,
                    activeColor = Color(0xFFF59E0B),
                    onClick = { isTorchOn = toggleTorch(context, !isTorchOn) }
                )

                QuickActionButton(
                    icon = Icons.Default.PhotoCamera,
                    label = "Camera",
                    onClick = { openCamera(context) }
                )

                QuickActionButton(
                    icon = Icons.Default.Calculate,
                    label = "Calc",
                    onClick = { openCalculator(context) }
                )

                QuickActionButton(
                    icon = Icons.Default.Wifi,
                    label = "Wi-Fi",
                    onClick = { openWifiSettings(context) }
                )

                QuickActionButton(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    onClick = { openSystemSettings(context) }
                )
            }
        }
        WidgetSize.EXPANDED -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickActionButton(
                        icon = if (isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                        label = if (isTorchOn) "Torch On" else "Torch",
                        isActive = isTorchOn,
                        activeColor = Color(0xFFF59E0B),
                        onClick = { isTorchOn = toggleTorch(context, !isTorchOn) }
                    )

                    QuickActionButton(
                        icon = Icons.Default.PhotoCamera,
                        label = "Camera",
                        onClick = { openCamera(context) }
                    )

                    QuickActionButton(
                        icon = Icons.Default.Calculate,
                        label = "Calculator",
                        onClick = { openCalculator(context) }
                    )

                    QuickActionButton(
                        icon = Icons.Default.Wifi,
                        label = "Wi-Fi",
                        onClick = { openWifiSettings(context) }
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Bluetooth,
                        label = "Bluetooth",
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                            } catch (_: Exception) {}
                        }
                    )

                    QuickActionButton(
                        icon = Icons.Default.Alarm,
                        label = "Alarms",
                        onClick = {
                            try {
                                context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                            } catch (_: Exception) {}
                        }
                    )

                    QuickActionButton(
                        icon = Icons.Default.BrightnessMedium,
                        label = "Display",
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                            } catch (_: Exception) {}
                        }
                    )

                    QuickActionButton(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        onClick = { openSystemSettings(context) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    isCompact: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = if (isCompact) 6.dp else 8.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isCompact) 32.dp else 36.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) activeColor else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(if (isCompact) 15.dp else 17.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = if (isCompact) 9.sp else 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

fun openCamera(context: Context) {
    try {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Opening Camera...", Toast.LENGTH_SHORT).show()
    }
}

fun openWifiSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun openSystemSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun toggleTorch(context: Context, turnOn: Boolean): Boolean {
    return try {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        val cameraId = cameraManager?.cameraIdList?.firstOrNull { id ->
            val chars = cameraManager.getCameraCharacteristics(id)
            chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        if (cameraId != null) {
            cameraManager.setTorchMode(cameraId, turnOn)
            turnOn
        } else {
            Toast.makeText(context, "Flashlight unavailable", Toast.LENGTH_SHORT).show()
            false
        }
    } catch (_: Exception) {
        false
    }
}
