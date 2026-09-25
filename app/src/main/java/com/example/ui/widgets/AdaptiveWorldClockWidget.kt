package com.example.ui.widgets

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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WidgetSize
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class WorldCity(
    val name: String,
    val timeZoneId: String,
    val code: String
)

val defaultWorldCities = listOf(
    WorldCity("Tokyo", "Asia/Tokyo", "TYO"),
    WorldCity("London", "Europe/London", "LON"),
    WorldCity("New York", "America/New_York", "NYC"),
    WorldCity("San Francisco", "America/Los_Angeles", "SFO"),
    WorldCity("Sydney", "Australia/Sydney", "SYD")
)

@Composable
fun AdaptiveWorldClockWidget(
    size: WidgetSize = WidgetSize.STANDARD,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Date()) }
    var selectedCityIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(10000L) // 10s tick is battery friendly
        }
    }

    val localTz = remember { TimeZone.getDefault() }
    val localTimeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).apply { timeZone = localTz } }

    when (size) {
        WidgetSize.COMPACT -> {
            val targetCity = defaultWorldCities[selectedCityIndex % defaultWorldCities.size]
            val cityTz = remember(targetCity) { TimeZone.getTimeZone(targetCity.timeZoneId) }
            val cityTimeFormat = remember(cityTz) { SimpleDateFormat("HH:mm", Locale.getDefault()).apply { timeZone = cityTz } }
            val offsetHours = calculateOffsetHours(localTz, cityTz)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { selectedCityIndex = (selectedCityIndex + 1) % defaultWorldCities.size }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Local ${localTimeFormat.format(currentTime)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${targetCity.name} ${cityTimeFormat.format(currentTime)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (offsetHours >= 0) "+${offsetHours}h" else "${offsetHours}h",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
        WidgetSize.STANDARD -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val cities = defaultWorldCities.take(3)
                cities.forEach { city ->
                    val cityTz = remember(city) { TimeZone.getTimeZone(city.timeZoneId) }
                    val cityTimeFormat = remember(cityTz) { SimpleDateFormat("HH:mm", Locale.getDefault()).apply { timeZone = cityTz } }
                    val isDaytime = remember(cityTz, currentTime) {
                        val hour = SimpleDateFormat("H", Locale.getDefault()).apply { timeZone = cityTz }.format(currentTime).toIntOrNull() ?: 12
                        hour in 6..19
                    }
                    val offsetHours = calculateOffsetHours(localTz, cityTz)

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isDaytime) Icons.Default.WbSunny else Icons.Default.Bedtime,
                                    contentDescription = null,
                                    tint = if (isDaytime) Color(0xFFF59E0B) else Color(0xFF818CF8),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = city.code,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cityTimeFormat.format(currentTime),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (offsetHours >= 0) "+${offsetHours}h" else "${offsetHours}h",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
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
                        .padding(bottom = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "World Clocks",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Local: ${localTimeFormat.format(currentTime)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val displayCities = defaultWorldCities.take(4)
                    displayCities.forEach { city ->
                        val cityTz = remember(city) { TimeZone.getTimeZone(city.timeZoneId) }
                        val cityTimeFormat = remember(cityTz) { SimpleDateFormat("HH:mm", Locale.getDefault()).apply { timeZone = cityTz } }
                        val cityDateFormat = remember(cityTz) { SimpleDateFormat("EEE", Locale.getDefault()).apply { timeZone = cityTz } }
                        val isDaytime = remember(cityTz, currentTime) {
                            val hour = SimpleDateFormat("H", Locale.getDefault()).apply { timeZone = cityTz }.format(currentTime).toIntOrNull() ?: 12
                            hour in 6..19
                        }
                        val offsetHours = calculateOffsetHours(localTz, cityTz)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = if (isDaytime) Icons.Default.WbSunny else Icons.Default.Bedtime,
                                    contentDescription = null,
                                    tint = if (isDaytime) Color(0xFFF59E0B) else Color(0xFF818CF8),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = city.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = cityDateFormat.format(currentTime),
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = cityTimeFormat.format(currentTime),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (offsetHours >= 0) "+${offsetHours}h" else "${offsetHours}h",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun calculateOffsetHours(localTz: TimeZone, targetTz: TimeZone): Int {
    val now = System.currentTimeMillis()
    val localOffset = localTz.getOffset(now)
    val targetOffset = targetTz.getOffset(now)
    return ((targetOffset - localOffset) / (1000 * 60 * 60))
}
