package com.example.media3ex.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.example.media3ex.R
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
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

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        initializeNotificationChannel()

        mediaSession =
            MediaSession
                .Builder(this, player)
                .setSessionActivity(sessionActivityPendingIntent)
                .setCallback(createSessionCallback())
                .build()

        // ✅ DefaultMediaNotificationProvider + 커스텀 버튼 레이아웃
        setMediaNotificationProvider(notificationProvider)

        // ✅ 커스텀 버튼 레이아웃 설정
        mediaSession?.setCustomLayout(createCustomLayout())
    }

    private fun createSessionCallback() =
        object : MediaSession.Callback {
            override fun onConnect(
                session: MediaSession,
                controller: MediaSession.ControllerInfo,
            ): MediaSession.ConnectionResult {
                val baseResult = super.onConnect(session, controller)

                // ✅ 커스텀 커맨드 등록
                val sessionCommands =
                    baseResult.availableSessionCommands
                        .buildUpon()
                        .add(SKIP_BACKWARD_COMMAND)
                        .add(SKIP_FORWARD_COMMAND)
                        .build()

                return MediaSession.ConnectionResult
                    .AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(sessionCommands)
                    .setAvailablePlayerCommands(baseResult.availablePlayerCommands)
                    .build()
            }

            override fun onCustomCommand(
                session: MediaSession,
                controller: MediaSession.ControllerInfo,
                customCommand: SessionCommand,
                args: Bundle,
            ): ListenableFuture<SessionResult> {
                when (customCommand.customAction) {
                    ACTION_SKIP_BACKWARD -> {
                        val newPosition = (player.currentPosition - 10000).coerceAtLeast(0)
                        player.seekTo(newPosition)
                        return Futures.immediateFuture(
                            SessionResult(SessionResult.RESULT_SUCCESS),
                        )
                    }

                    ACTION_SKIP_FORWARD -> {
                        val newPosition =
                            (player.currentPosition + 10000)
                                .coerceAtMost(player.duration)
                        player.seekTo(newPosition)
                        return Futures.immediateFuture(
                            SessionResult(SessionResult.RESULT_SUCCESS),
                        )
                    }
                }
                return super.onCustomCommand(session, controller, customCommand, args)
            }
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

        return ImmutableList.of(skipBackwardButton, skipForwardButton)
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

        const val ACTION_SKIP_BACKWARD = "action_skip_backward"
        const val ACTION_SKIP_FORWARD = "action_skip_forward"

        val SKIP_BACKWARD_COMMAND = SessionCommand(ACTION_SKIP_BACKWARD, Bundle.EMPTY)
        val SKIP_FORWARD_COMMAND = SessionCommand(ACTION_SKIP_FORWARD, Bundle.EMPTY)
    }
}
