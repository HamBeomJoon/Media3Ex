package com.example.media3ex.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.media3ex.R
import com.example.media3ex.service.PlaybackSessionCallback.Companion.SKIP_BACKWARD_COMMAND
import com.example.media3ex.service.PlaybackSessionCallback.Companion.SKIP_FORWARD_COMMAND
import com.google.common.collect.ImmutableList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@UnstableApi
class AudioPlaybackService : MediaSessionService() {
    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var sessionActivityPendingIntent: PendingIntent

    @Inject
    lateinit var notificationProvider: CustomMediaNotificationProvider

    @Inject
    lateinit var sessionCallback: PlaybackSessionCallback

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        initializeNotificationChannel()

        mediaSession =
            MediaSession
                .Builder(this, player)
                .setSessionActivity(sessionActivityPendingIntent)
                .setCallback(sessionCallback)
                .build()

        // ✅ DefaultMediaNotificationProvider + 커스텀 버튼 레이아웃
        setMediaNotificationProvider(notificationProvider)

        // ✅ 커스텀 버튼 레이아웃 설정
        mediaSession?.setCustomLayout(createCustomLayout())
    }

    private fun createCustomLayout(): ImmutableList<CommandButton> {
        val skipBackwardButton =
            CommandButton
                .Builder(CommandButton.ICON_SKIP_BACK_10)
                .setDisplayName("10초 뒤로")
                .setSessionCommand(SKIP_BACKWARD_COMMAND)
                .setCustomIconResId(R.drawable.ic_back_ten)
                .build()

        val skipForwardButton =
            CommandButton
                .Builder(CommandButton.ICON_SKIP_FORWARD_10)
                .setDisplayName("10초 앞으로")
                .setSessionCommand(SKIP_FORWARD_COMMAND)
                .setCustomIconResId(R.drawable.ic_forward_ten)
                .build()

        return ImmutableList.of(skipForwardButton, skipBackwardButton)
    }

    private fun initializeNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Audio playback controls"
                    setShowBadge(false)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player?.playWhenReady == false) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "audio_playback_channel_v2"
        const val CHANNEL_NAME = "Audio Playback"
    }
}
