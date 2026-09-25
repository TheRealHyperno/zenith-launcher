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

@Composable
fun SystemStatsWidget(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var stats by remember { mutableStateOf(readDeviceStats(context)) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                // Refresh on user click with no background loop needed
                stats = readDeviceStats(context)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
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

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // RAM Gauge
                StatBarItem(
                    icon = Icons.Default.Memory,
                    label = "RAM Free",
                    valueText = "${stats.freeRamMb} MB",
                    fraction = stats.ramUsedFraction,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                // Battery Gauge
                StatBarItem(
                    icon = Icons.Default.BatteryChargingFull,
                    label = "Battery",
                    valueText = "${stats.batteryPct}%",
                    fraction = stats.batteryPct / 100f,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )

                // Storage Gauge
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
            fontSize = 12.sp,
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
    val ramUsedFraction: Float,
    val batteryPct: Int,
    val freeStorageGb: Long,
    val storageUsedFraction: Float
)

fun readDeviceStats(context: Context): DeviceStats {
    // RAM
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    actManager?.getMemoryInfo(memInfo)
    val freeRamMb = (memInfo.availMem / (1024 * 1024))
    val totalRamMb = (memInfo.totalMem / (1024 * 1024)).coerceAtLeast(1)
    val ramUsedFraction = (totalRamMb - freeRamMb).toFloat() / totalRamMb.toFloat()

    // Battery
    val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    val batteryPct = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)?.coerceIn(0, 100) ?: 80

    // Storage
    val stat = StatFs(Environment.getDataDirectory().path)
    val bytesAvailable = stat.availableBytes
    val bytesTotal = stat.totalBytes.coerceAtLeast(1)
    val freeStorageGb = bytesAvailable / (1024 * 1024 * 1024)
    val storageUsedFraction = (bytesTotal - bytesAvailable).toFloat() / bytesTotal.toFloat()

    return DeviceStats(
        freeRamMb = freeRamMb,
        ramUsedFraction = ramUsedFraction,
        batteryPct = batteryPct,
        freeStorageGb = freeStorageGb,
        storageUsedFraction = storageUsedFraction
    )
}
