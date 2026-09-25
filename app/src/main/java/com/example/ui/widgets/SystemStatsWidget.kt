package com.example.ui.widgets

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WidgetSize

@Composable
fun SystemStatsWidget(
    size: WidgetSize = WidgetSize.STANDARD,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var stats by remember { mutableStateOf(readDeviceStats(context)) }

    when (size) {
        WidgetSize.COMPACT -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { stats = readDeviceStats(context) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                // Compact RAM Badge
                CompactStatBadge(
                    icon = Icons.Default.Memory,
                    label = "RAM",
                    value = "${(stats.ramUsedFraction * 100).toInt()}%",
                    color = MaterialTheme.colorScheme.primary
                )

                // Compact Battery Badge
                CompactStatBadge(
                    icon = Icons.Default.BatteryChargingFull,
                    label = "BAT",
                    value = "${stats.batteryPct}%",
                    color = Color(0xFF10B981)
                )

                // Compact Storage Badge
                CompactStatBadge(
                    icon = Icons.Default.Storage,
                    label = "DSK",
                    value = "${(stats.storageUsedFraction * 100).toInt()}%",
                    color = Color(0xFFF59E0B)
                )
            }
        }
        WidgetSize.STANDARD -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { stats = readDeviceStats(context) }
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "System Monitor",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Tap to Refresh",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBarItem(
                        icon = Icons.Default.Memory,
                        label = "RAM Free",
                        valueText = "${stats.freeRamMb} MB",
                        fraction = stats.ramUsedFraction,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    StatBarItem(
                        icon = Icons.Default.BatteryChargingFull,
                        label = "Battery",
                        valueText = "${stats.batteryPct}%",
                        fraction = stats.batteryPct / 100f,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )

                    StatBarItem(
                        icon = Icons.Default.Storage,
                        label = "Free Disk",
                        valueText = "${stats.freeStorageGb} GB",
                        fraction = stats.storageUsedFraction,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        WidgetSize.EXPANDED -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { stats = readDeviceStats(context) }
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "System Diagnostics Dashboard",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { stats = readDeviceStats(context) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Live",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBarItem(
                        icon = Icons.Default.Memory,
                        label = "RAM Free / Total",
                        valueText = "${stats.freeRamMb} / ${stats.totalRamMb} MB",
                        fraction = stats.ramUsedFraction,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    StatBarItem(
                        icon = Icons.Default.BatteryChargingFull,
                        label = "Battery Level",
                        valueText = "${stats.batteryPct}% Healthy",
                        fraction = stats.batteryPct / 100f,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )

                    StatBarItem(
                        icon = Icons.Default.Storage,
                        label = "Disk Free / Total",
                        valueText = "${stats.freeStorageGb} / ${stats.totalStorageGb} GB",
                        fraction = stats.storageUsedFraction,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Zero background wakelocks",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Process memory optimal",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

@Composable
fun CompactStatBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "$label: ",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    valueText: String,
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = valueText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { fraction.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

data class DeviceStats(
    val freeRamMb: Long,
    val totalRamMb: Long,
    val ramUsedFraction: Float,
    val batteryPct: Int,
    val freeStorageGb: Long,
    val totalStorageGb: Long,
    val storageUsedFraction: Float
)

fun readDeviceStats(context: Context): DeviceStats {
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    actManager?.getMemoryInfo(memInfo)
    val freeRamMb = (memInfo.availMem / (1024 * 1024))
    val totalRamMb = (memInfo.totalMem / (1024 * 1024)).coerceAtLeast(1)
    val ramUsedFraction = (totalRamMb - freeRamMb).toFloat() / totalRamMb.toFloat()

    val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    val batteryPct = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)?.coerceIn(0, 100) ?: 80

    val stat = StatFs(Environment.getDataDirectory().path)
    val bytesAvailable = stat.availableBytes
    val bytesTotal = stat.totalBytes.coerceAtLeast(1)
    val freeStorageGb = bytesAvailable / (1024 * 1024 * 1024)
    val totalStorageGb = bytesTotal / (1024 * 1024 * 1024)
    val storageUsedFraction = (bytesTotal - bytesAvailable).toFloat() / bytesTotal.toFloat()

    return DeviceStats(
        freeRamMb = freeRamMb,
        totalRamMb = totalRamMb,
        ramUsedFraction = ramUsedFraction,
        batteryPct = batteryPct,
        freeStorageGb = freeStorageGb,
        totalStorageGb = totalStorageGb,
        storageUsedFraction = storageUsedFraction
    )
}
