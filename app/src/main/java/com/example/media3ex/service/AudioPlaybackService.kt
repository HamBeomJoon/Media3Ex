package com.example.media3ex.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
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
        val notification = createCustomNotification()
        notificationManager.notify(NOTIFICATION_ID, notification)

        // 포그라운드 서비스로 실행
        if (player.isPlaying) {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    @OptIn(UnstableApi::class)
    private fun createCustomNotification(): Notification {
        val mediaMetadata = player.currentMediaItem?.mediaMetadata

        // 제목과 아티스트 가져오기
        val title = mediaMetadata?.title?.toString() ?: "재생 중"
        val artist = mediaMetadata?.artist?.toString() ?: "알 수 없는 아티스트"

        // RemoteViews 생성
        val notificationLayout = RemoteViews(packageName, R.layout.notification_audio_player)

        // 제목과 아티스트 설정
        notificationLayout.setTextViewText(R.id.notification_title, title)
        notificationLayout.setTextViewText(R.id.notification_artist, artist)

        // 재생/일시정지 버튼 아이콘 설정
        val playPauseIcon =
            if (player.isPlaying) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }
        notificationLayout.setImageViewResource(R.id.btn_play_pause, playPauseIcon)

        // 버튼 클릭 이벤트 설정
        notificationLayout.setOnClickPendingIntent(
            R.id.btn_play_pause,
            createPendingIntent(ACTION_PLAY_PAUSE),
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btn_previous,
            createPendingIntent(ACTION_PREVIOUS),
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btn_next,
            createPendingIntent(ACTION_NEXT),
        )

        val builder =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setContentIntent(mediaSession?.sessionActivity)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(player.isPlaying)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            builder
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
        } else {
            builder
                .setContentTitle(title)
                .setContentText(artist)
                .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession!!))
        }

        return builder.build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent =
            Intent(this, AudioPlaybackService::class.java).apply {
                this.action = action
            }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            }

            ACTION_PREVIOUS -> player.seekToPrevious()
            ACTION_NEXT -> player.seekToNext()
        }
        return super.onStartCommand(intent, flags, startId)
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
        const val ACTION_PLAY_PAUSE = "action_play_pause"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_NEXT = "action_next"
    }
}
