package com.example.service

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class MediaTrackInfo(
    val title: String = "No track playing...",
    val artist: String = "Tap to open Spotify",
    val album: String = "",
    val packageName: String = "com.spotify.music",
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val albumArtBitmap: Bitmap? = null,
    val hasActiveSession: Boolean = false,
    val appName: String = "Spotify",
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val hasTrack: Boolean get() = hasActiveSession && title.isNotBlank() && title != "No track playing..."
}

object ZenithMediaManager {
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val _mediaTrack = MutableStateFlow(
        MediaTrackInfo(
            title = "No track playing...",
            artist = "Tap to open Spotify",
            album = "",
            packageName = "com.spotify.music",
            isPlaying = false,
            positionMs = 0L,
            durationMs = 0L,
            hasActiveSession = false,
            appName = "Spotify"
        )
    )
    val mediaTrack: StateFlow<MediaTrackInfo> = _mediaTrack.asStateFlow()

    private var activeController: MediaController? = null
    private var notificationListenerRef: ZenithNotificationListener? = null
    private var activeSessionsChangedListener: MediaSessionManager.OnActiveSessionsChangedListener? = null
    private var tickerJob: Job? = null
    private var isReceiverRegistered = false

    private val controllerCallback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            updateFromMetadata(metadata)
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            updateFromPlaybackState(state)
        }

        override fun onSessionDestroyed() {
            activeController = null
            _mediaTrack.value = MediaTrackInfo(
                title = "No track playing...",
                artist = "Tap to open Spotify",
                album = "",
                packageName = "com.spotify.music",
                isPlaying = false,
                positionMs = 0L,
                durationMs = 0L,
                albumArtBitmap = null,
                hasActiveSession = false,
                appName = "Spotify"
            )
            tickerJob?.cancel()
        }
    }

    private val spotifyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            val action = intent.action ?: return

            when (action) {
                "com.spotify.music.playbackstatechanged",
                "com.android.music.playstatechanged" -> {
                    val isPlaying = intent.getBooleanExtra("playing", false)
                    val pos = intent.getIntExtra("playbackPosition", 0).toLong()

                    val current = _mediaTrack.value
                    if (current.title == "No track playing..." && !isPlaying) {
                        // Keep as no track
                        return
                    }

                    _mediaTrack.value = current.copy(
                        isPlaying = isPlaying,
                        positionMs = if (pos > 0) pos else current.positionMs,
                        hasActiveSession = true,
                        appName = "Spotify",
                        packageName = "com.spotify.music"
                    )
                    startOrStopTicker(isPlaying)
                }
                "com.spotify.music.metadatachanged",
                "com.android.music.metachanged" -> {
                    val track = intent.getStringExtra("track") ?: intent.getStringExtra("title")
                    val artist = intent.getStringExtra("artist")
                    val album = intent.getStringExtra("album")
                    val length = intent.getIntExtra("length", 0).toLong()

                    if (!track.isNullOrBlank()) {
                        _mediaTrack.value = _mediaTrack.value.copy(
                            title = track,
                            artist = artist ?: "Spotify",
                            album = album ?: "",
                            durationMs = if (length > 0) length else _mediaTrack.value.durationMs,
                            hasActiveSession = true,
                            appName = "Spotify",
                            packageName = "com.spotify.music"
                        )
                    } else {
                        resetToNoTrack()
                    }
                }
            }
        }
    }

    fun init(context: Context) {
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction("com.spotify.music.playbackstatechanged")
                addAction("com.spotify.music.metadatachanged")
                addAction("com.spotify.music.queuechanged")
                addAction("com.android.music.metachanged")
                addAction("com.android.music.playstatechanged")
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.applicationContext.registerReceiver(
                        spotifyReceiver,
                        filter,
                        Context.RECEIVER_EXPORTED
                    )
                } else {
                    context.applicationContext.registerReceiver(spotifyReceiver, filter)
                }
                isReceiverRegistered = true
            } catch (_: Exception) {}
        }
    }

    fun onNotificationListenerConnected(listener: ZenithNotificationListener) {
        notificationListenerRef = listener
        try {
            val mediaSessionManager = listener.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
            val component = ComponentName(listener, ZenithNotificationListener::class.java)

            activeSessionsChangedListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
                findAndBindBestSession(listener, controllers)
            }

            mediaSessionManager?.addOnActiveSessionsChangedListener(
                activeSessionsChangedListener!!,
                component
            )

            val sessions = mediaSessionManager?.getActiveSessions(component)
            findAndBindBestSession(listener, sessions)
        } catch (_: Exception) {
            // Permission or OEM limitation
        }
    }

    fun onNotificationListenerDisconnected() {
        try {
            activeController?.unregisterCallback(controllerCallback)
        } catch (_: Exception) {}
        activeController = null
        notificationListenerRef = null
        resetToNoTrack()
    }

    fun onMediaSessionTokenReceived(context: Context, token: MediaSession.Token, pkg: String) {
        try {
            val controller = MediaController(context, token)
            bindController(context, controller, pkg)
        } catch (_: Exception) {}
    }

    fun onMediaNotificationExtracted(
        title: String?,
        artist: String?,
        album: String?,
        artBitmap: Bitmap?,
        pkg: String,
        isPlaying: Boolean
    ) {
        if (!title.isNullOrBlank()) {
            _mediaTrack.value = _mediaTrack.value.copy(
                title = title,
                artist = if (!artist.isNullOrBlank()) artist else resolveAppName(pkg),
                album = album ?: "",
                albumArtBitmap = artBitmap ?: _mediaTrack.value.albumArtBitmap,
                packageName = pkg,
                appName = resolveAppName(pkg),
                isPlaying = isPlaying,
                hasActiveSession = true
            )
            startOrStopTicker(isPlaying)
        } else {
            resetToNoTrack()
        }
    }

    fun resetToNoTrack() {
        if (activeController == null) {
            _mediaTrack.value = MediaTrackInfo(
                title = "No track playing...",
                artist = "Tap to open Spotify",
                album = "",
                packageName = "com.spotify.music",
                isPlaying = false,
                positionMs = 0L,
                durationMs = 0L,
                albumArtBitmap = null,
                hasActiveSession = false,
                appName = "Spotify"
            )
            tickerJob?.cancel()
        }
    }

    private fun findAndBindBestSession(context: Context, controllers: List<MediaController>?) {
        if (controllers.isNullOrEmpty()) {
            resetToNoTrack()
            return
        }

        // Prefer currently playing session, or Spotify session
        val best = controllers.firstOrNull {
            it.playbackState?.state == PlaybackState.STATE_PLAYING
        } ?: controllers.firstOrNull {
            it.packageName == "com.spotify.music"
        } ?: controllers.firstOrNull()

        if (best != null) {
            bindController(context, best, best.packageName)
        } else {
            resetToNoTrack()
        }
    }

    private fun bindController(context: Context, controller: MediaController, pkg: String) {
        try {
            activeController?.unregisterCallback(controllerCallback)
        } catch (_: Exception) {}

        activeController = controller
        try {
            controller.registerCallback(controllerCallback)
        } catch (_: Exception) {}

        val metadata = controller.metadata
        val playbackState = controller.playbackState

        updateFromMetadata(metadata, pkg)
        updateFromPlaybackState(playbackState)
    }

    private fun updateFromMetadata(metadata: MediaMetadata?, fallbackPkg: String? = null) {
        if (metadata == null) {
            if (activeController?.playbackState == null) {
                resetToNoTrack()
            }
            return
        }
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
        val album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM)
        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
        val artBitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: metadata.getBitmap(MediaMetadata.METADATA_KEY_ART)

        val pkg = activeController?.packageName ?: fallbackPkg ?: _mediaTrack.value.packageName

        if (!title.isNullOrBlank()) {
            _mediaTrack.value = _mediaTrack.value.copy(
                title = title,
                artist = if (!artist.isNullOrBlank()) artist else resolveAppName(pkg),
                album = album ?: "",
                durationMs = if (duration > 0) duration else _mediaTrack.value.durationMs,
                albumArtBitmap = artBitmap ?: _mediaTrack.value.albumArtBitmap,
                packageName = pkg,
                appName = resolveAppName(pkg),
                hasActiveSession = true
            )
        } else {
            resetToNoTrack()
        }
    }

    private fun updateFromPlaybackState(playbackState: PlaybackState?) {
        if (playbackState == null) return

        if (playbackState.state == PlaybackState.STATE_STOPPED || playbackState.state == PlaybackState.STATE_NONE) {
            _mediaTrack.value = _mediaTrack.value.copy(
                title = "No track playing...",
                artist = "Tap to open Spotify",
                isPlaying = false,
                positionMs = 0L,
                durationMs = 0L,
                hasActiveSession = false
            )
            tickerJob?.cancel()
            return
        }

        val isPlaying = playbackState.state == PlaybackState.STATE_PLAYING
        val position = playbackState.position

        _mediaTrack.value = _mediaTrack.value.copy(
            isPlaying = isPlaying,
            positionMs = if (position >= 0) position else _mediaTrack.value.positionMs,
            hasActiveSession = true
        )

        startOrStopTicker(isPlaying)
    }

    private fun startOrStopTicker(isPlaying: Boolean) {
        tickerJob?.cancel()
        if (isPlaying) {
            tickerJob = scope.launch {
                while (isActive) {
                    delay(1000L)
                    val current = _mediaTrack.value
                    if (current.isPlaying) {
                        val newPos = current.positionMs + 1000L
                        if (current.durationMs > 0 && newPos >= current.durationMs) {
                            _mediaTrack.value = current.copy(positionMs = current.durationMs)
                        } else {
                            _mediaTrack.value = current.copy(positionMs = newPos)
                        }
                    }
                }
            }
        }
    }

    fun togglePlayPause(context: Context) {
        val current = _mediaTrack.value
        val controller = activeController

        if (controller != null) {
            try {
                if (current.isPlaying) {
                    controller.transportControls.pause()
                } else {
                    controller.transportControls.play()
                }
                return
            } catch (_: Exception) {}
        }

        // If no active session or track, open Spotify so user can select music
        if (!current.hasTrack) {
            launchPlayerApp(context)
            sendMediaKeyEvent(context, KeyEvent.KEYCODE_MEDIA_PLAY)
            return
        }

        // Secondary dispatch via AudioManager Media Button
        sendMediaKeyEvent(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
    }

    fun skipToNext(context: Context) {
        val controller = activeController
        if (controller != null) {
            try {
                controller.transportControls.skipToNext()
                return
            } catch (_: Exception) {}
        }
        sendMediaKeyEvent(context, KeyEvent.KEYCODE_MEDIA_NEXT)
    }

    fun skipToPrevious(context: Context) {
        val controller = activeController
        if (controller != null) {
            try {
                controller.transportControls.skipToPrevious()
                return
            } catch (_: Exception) {}
        }
        sendMediaKeyEvent(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
    }

    fun seekTo(positionMs: Long) {
        val controller = activeController
        if (controller != null) {
            try {
                controller.transportControls.seekTo(positionMs)
            } catch (_: Exception) {}
        }
        _mediaTrack.value = _mediaTrack.value.copy(positionMs = positionMs)
    }

    fun toggleShuffle() {
        val current = _mediaTrack.value
        _mediaTrack.value = current.copy(isShuffle = !current.isShuffle)
    }

    fun toggleRepeat() {
        val current = _mediaTrack.value
        _mediaTrack.value = current.copy(isRepeat = !current.isRepeat)
    }

    fun launchPlayerApp(context: Context) {
        val pkg = _mediaTrack.value.packageName.ifBlank { "com.spotify.music" }
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            }
        } catch (_: Exception) {}

        // If Spotify is not installed, open Spotify in Google Play Store
        try {
            val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.spotify.music")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(storeIntent)
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    private fun sendMediaKeyEvent(context: Context, keyCode: Int) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            val eventDown = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            audioManager.dispatchMediaKeyEvent(eventDown)
            val eventUp = KeyEvent(KeyEvent.ACTION_UP, keyCode)
            audioManager.dispatchMediaKeyEvent(eventUp)
        } catch (_: Exception) {}
    }

    private fun resolveAppName(packageName: String): String {
        return when {
            packageName.contains("spotify", ignoreCase = true) -> "Spotify"
            packageName.contains("youtube", ignoreCase = true) -> "YouTube Music"
            packageName.contains("apple", ignoreCase = true) -> "Apple Music"
            packageName.contains("amazon", ignoreCase = true) -> "Amazon Music"
            packageName.contains("deezer", ignoreCase = true) -> "Deezer"
            packageName.contains("tidal", ignoreCase = true) -> "Tidal"
            packageName.contains("soundcloud", ignoreCase = true) -> "SoundCloud"
            else -> "Media Player"
        }
    }
}
