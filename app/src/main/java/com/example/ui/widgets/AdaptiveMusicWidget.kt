package com.example.ui.widgets

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.Settings
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WidgetSize
import com.example.service.NotificationBadgeManager
import com.example.service.ZenithMediaManager

private val SpotifyGreen = Color(0xFF1DB954)
private val SpotifyDarkGreen = Color(0xFF169C46)

@Composable
fun AdaptiveMusicWidget(
    size: WidgetSize = WidgetSize.STANDARD,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val trackInfo by ZenithMediaManager.mediaTrack.collectAsState()
    val isNotificationConnected by NotificationBadgeManager.isServiceConnected.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (trackInfo.isPlaying) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "album_art_pulse"
    )

    when (size) {
        WidgetSize.COMPACT -> {
            CompactMusicWidget(
                trackInfo = trackInfo,
                onPlayPause = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.togglePlayPause(context)
                },
                onNext = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.skipToNext(context)
                },
                onLaunchApp = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.launchPlayerApp(context)
                },
                modifier = modifier
            )
        }
        WidgetSize.STANDARD -> {
            StandardMusicWidget(
                trackInfo = trackInfo,
                isNotificationConnected = isNotificationConnected,
                pulseScale = pulseScale,
                onPlayPause = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.togglePlayPause(context)
                },
                onPrevious = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.skipToPrevious(context)
                },
                onNext = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.skipToNext(context)
                },
                onSeek = { posMs ->
                    ZenithMediaManager.seekTo(posMs)
                },
                onLaunchApp = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.launchPlayerApp(context)
                },
                onOpenNotificationSettings = {
                    try {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    } catch (_: Exception) {}
                },
                modifier = modifier
            )
        }
        WidgetSize.EXPANDED -> {
            ExpandedMusicWidget(
                trackInfo = trackInfo,
                isNotificationConnected = isNotificationConnected,
                pulseScale = pulseScale,
                onPlayPause = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.togglePlayPause(context)
                },
                onPrevious = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.skipToPrevious(context)
                },
                onNext = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.skipToNext(context)
                },
                onSeek = { posMs ->
                    ZenithMediaManager.seekTo(posMs)
                },
                onToggleShuffle = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.toggleShuffle()
                },
                onToggleRepeat = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.toggleRepeat()
                },
                onLaunchApp = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    ZenithMediaManager.launchPlayerApp(context)
                },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun CompactMusicWidget(
    trackInfo: com.example.service.MediaTrackInfo,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onLaunchApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onLaunchApp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // Thumbnail & Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            AlbumArtThumbnail(
                bitmap = trackInfo.albumArtBitmap,
                sizeDp = 38,
                fallbackColor = SpotifyGreen
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trackInfo.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = trackInfo.artist,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Playback Controls
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SpotifyGreen)
            ) {
                Icon(
                    imageVector = if (trackInfo.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (trackInfo.isPlaying) "Pause" else "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = onNext,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next Track",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun StandardMusicWidget(
    trackInfo: com.example.service.MediaTrackInfo,
    isNotificationConnected: Boolean,
    pulseScale: Float,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onLaunchApp: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // Top Row: Album Art + Song Info + App Source Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLaunchApp)
        ) {
            Box(
                modifier = Modifier
                    .scale(if (trackInfo.isPlaying) pulseScale else 1.0f)
            ) {
                AlbumArtThumbnail(
                    bitmap = trackInfo.albumArtBitmap,
                    sizeDp = 58,
                    fallbackColor = SpotifyGreen
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trackInfo.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = trackInfo.artist,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (trackInfo.album.isNotBlank()) {
                    Text(
                        text = trackInfo.album,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // App badge pill (Spotify)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SpotifyGreen.copy(alpha = 0.16f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpotifyGreen.copy(alpha = 0.35f)),
                modifier = Modifier.clickable(onClick = onLaunchApp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = SpotifyGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trackInfo.appName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpotifyGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar with timestamps
        val duration = trackInfo.durationMs
        val progress = if (duration > 0) (trackInfo.positionMs.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
        var sliderPosition by remember(progress) { mutableFloatStateOf(progress) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = sliderPosition,
                onValueChange = { if (duration > 0) sliderPosition = it },
                onValueChangeFinished = {
                    if (duration > 0) {
                        onSeek((sliderPosition * duration).toLong())
                    }
                },
                enabled = duration > 0,
                colors = SliderDefaults.colors(
                    thumbColor = SpotifyGreen,
                    activeTrackColor = SpotifyGreen,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (duration > 0) formatMillis(trackInfo.positionMs) else "00:00",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = if (duration > 0) formatMillis(duration) else "--:--",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Transport Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Surface(
                shape = CircleShape,
                color = SpotifyGreen,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onPlayPause)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (trackInfo.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (trackInfo.isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Notification permission hint if not connected
        if (!isNotificationConnected && !trackInfo.hasActiveSession) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenNotificationSettings)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Enable notification access for live QS media sync",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandedMusicWidget(
    trackInfo: com.example.service.MediaTrackInfo,
    isNotificationConnected: Boolean,
    pulseScale: Float,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onLaunchApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // App header bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onLaunchApp)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(SpotifyGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(11.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = trackInfo.appName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpotifyGreen
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speaker,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Phone Speaker",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        // Center: Album Art + Detailed Track Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLaunchApp)
        ) {
            Box(
                modifier = Modifier
                    .scale(if (trackInfo.isPlaying) pulseScale else 1.0f)
            ) {
                AlbumArtThumbnail(
                    bitmap = trackInfo.albumArtBitmap,
                    sizeDp = 80,
                    fallbackColor = SpotifyGreen
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trackInfo.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = trackInfo.artist,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                if (trackInfo.album.isNotBlank()) {
                    Text(
                        text = trackInfo.album,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress bar with current and remaining time
        val duration = trackInfo.durationMs
        val progress = if (duration > 0) (trackInfo.positionMs.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
        var sliderPosition by remember(progress) { mutableFloatStateOf(progress) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = sliderPosition,
                onValueChange = { if (duration > 0) sliderPosition = it },
                onValueChangeFinished = {
                    if (duration > 0) {
                        onSeek((sliderPosition * duration).toLong())
                    }
                },
                enabled = duration > 0,
                colors = SliderDefaults.colors(
                    thumbColor = SpotifyGreen,
                    activeTrackColor = SpotifyGreen,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (duration > 0) formatMillis(trackInfo.positionMs) else "00:00",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                val remaining = if (duration > 0) (duration - trackInfo.positionMs).coerceAtLeast(0L) else 0L
                Text(
                    text = if (duration > 0) "-${formatMillis(remaining)}" else "--:--",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Full 5-button transport controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleShuffle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (trackInfo.isShuffle) SpotifyGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onPrevious,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Big Play/Pause Button
            Surface(
                shape = CircleShape,
                color = SpotifyGreen,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onPlayPause)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (trackInfo.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (trackInfo.isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = onToggleRepeat,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = if (trackInfo.isRepeat) SpotifyGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AlbumArtThumbnail(
    bitmap: Bitmap?,
    sizeDp: Int,
    fallbackColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 3.dp,
        modifier = modifier.size(sizeDp.dp)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(sizeDp.dp)
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(sizeDp.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                fallbackColor.copy(alpha = 0.85f),
                                Color(0xFF0F172A)
                            )
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size((sizeDp / 2).dp)
                )
            }
        }
    }
}

private fun formatMillis(millis: Long): String {
    if (millis <= 0L) return "00:00"
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
