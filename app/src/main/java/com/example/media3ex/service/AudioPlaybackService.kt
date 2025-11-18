package com.example.media3ex.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.media3ex.R
import com.example.media3ex.presentation.MainActivity

class AudioPlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        initializeNotificationChannel()
        initializeSessionAndPlayer()
    }

    private fun initializeNotificationChannel() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    description = "Audio playback controls"
                    setShowBadge(false)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeSessionAndPlayer() {
        player =
            ExoPlayer
                .Builder(this)
                .build()
                .apply {
                    playWhenReady = false
                }

        // 알림 클릭 시 앱으로 이동하는 PendingIntent
        val sessionActivityPendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        mediaSession =
            MediaSession
                .Builder(this, player)
                .setSessionActivity(sessionActivityPendingIntent)
                .build()

        // Player 리스너 추가 - 메타데이터 변경 감지
        player.addListener(
            object : Player.Listener {
                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    updateNotification()
                }

                override fun onPlayWhenReadyChanged(
                    playWhenReady: Boolean,
                    reason: Int,
                ) {
                    updateNotification()
                }
            },
        )
    }

    @OptIn(UnstableApi::class)
    private fun updateNotification() {
        val notification = createNotification()
        notificationManager.notify(NOTIFICATION_ID, notification)

        // 포그라운드 서비스로 실행
        if (player.isPlaying) {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    @OptIn(UnstableApi::class)
    private fun createNotification(): Notification {
        val mediaMetadata = player.currentMediaItem?.mediaMetadata

        // 제목과 아티스트 가져오기
        val title = mediaMetadata?.title?.toString() ?: "재생 중"
        val artist = mediaMetadata?.artist?.toString() ?: "알 수 없는 아티스트"

        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(
                mediaMetadata?.artworkData?.let {
                    android.graphics.BitmapFactory.decodeByteArray(it, 0, it.size)
                },
            ) // 앨범 아트
            .setColor(ContextCompat.getColor(this, R.color.hearit_purple1)) // 배경색
            .setColorized(true) // 배경색 활성화
            .setStyle(
                MediaStyleNotificationHelper
                    .MediaStyle(mediaSession!!)
                    .setShowActionsInCompactView(0, 1, 2),
            ).setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setContentIntent(mediaSession?.sessionActivity)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player?.playWhenReady == false) {
            // 재생 중이 아니면 서비스 종료
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
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "audio_playback_channel"
        private const val CHANNEL_NAME = "Audio Playback"
    }
}
