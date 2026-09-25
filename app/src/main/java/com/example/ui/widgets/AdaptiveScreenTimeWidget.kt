package com.example.ui.widgets

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.provider.Settings
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsPaused
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WidgetSize

private val WellbeingPurple = Color(0xFFA855F7)
private val WellbeingCyan = Color(0xFF06B6D4)

@Composable
fun AdaptiveScreenTimeWidget(
    size: WidgetSize = WidgetSize.STANDARD,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("zenith_screen_time_prefs", Context.MODE_PRIVATE) }

    // Screen time estimation
    val screenTimeMinutes = remember { prefs.getInt("screen_time_mins", 134) } // e.g. 2h 14m
    val unlockCount = remember { prefs.getInt("unlock_count", 28) }
    var isFocusModeActive by remember { mutableStateOf(prefs.getBoolean("focus_mode", false)) }

    val hours = screenTimeMinutes / 60
    val mins = screenTimeMinutes % 60
    val targetMinutes = 240 // 4 hours goal
    val progress = (screenTimeMinutes.toFloat() / targetMinutes.toFloat()).coerceIn(0f, 1f)

    when (size) {
        WidgetSize.COMPACT -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { openDigitalWellbeingSettings(context) }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(WellbeingPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = WellbeingPurple,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${hours}h ${mins}m screen time",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$unlockCount unlocks",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            }
        }
        WidgetSize.STANDARD -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { openDigitalWellbeingSettings(context) }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(WellbeingPurple.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassBottom,
                                contentDescription = null,
                                tint = WellbeingPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "${hours}h ${mins}m",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Screen Time Today",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = WellbeingPurple.copy(alpha = 0.16f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = WellbeingPurple,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$unlockCount unlocks",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WellbeingPurple
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = WellbeingPurple,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Goal: Under 4 hrs",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "${((1f - progress) * 4).coerceAtLeast(0f).toInt()}h left in budget",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (progress < 0.85f) WellbeingCyan else Color(0xFFEF4444)
                    )
                }
            }
        }
        WidgetSize.EXPANDED -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "Digital Wellbeing",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${hours}h ${mins}m screen time",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WellbeingPurple
                    )
                }

                // Top 3 Apps Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppUsagePill("Spotify", "48m", 0.35f, Color(0xFF1DB954), Modifier.weight(1f))
                    AppUsagePill("YouTube", "42m", 0.30f, Color(0xFFEF4444), Modifier.weight(1f))
                    AppUsagePill("Chrome", "24m", 0.18f, Color(0xFF3B82F6), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Focus Mode Action Toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isFocusModeActive) WellbeingPurple.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isFocusModeActive = !isFocusModeActive
                            prefs.edit().putBoolean("focus_mode", isFocusModeActive).apply()
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isFocusModeActive) Icons.Default.NotificationsPaused else Icons.Default.SelfImprovement,
                                contentDescription = null,
                                tint = if (isFocusModeActive) WellbeingPurple else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isFocusModeActive) "Focus Mode Active" else "Zenith Focus Mode",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isFocusModeActive) "Distracting apps quieted" else "Tap to pause non-essential apps",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                                )
                            }
                        }

                        Text(
                            text = if (isFocusModeActive) "ON" else "OFF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isFocusModeActive) WellbeingPurple else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppUsagePill(
    appName: String,
    timeText: String,
    usageFraction: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)) {
            Text(
                text = appName,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = timeText,
                fontSize = 10.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { usageFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp)),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

private fun openDigitalWellbeingSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}
