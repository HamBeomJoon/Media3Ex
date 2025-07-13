package com.example.viewbindingex.musicService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.viewbindingex.R
import com.example.viewbindingex.musicService.MusicNotificationReceiver.Companion.ACTION_PAUSE
import com.example.viewbindingex.musicService.MusicNotificationReceiver.Companion.ACTION_PLAY
import com.example.viewbindingex.musicService.MusicNotificationReceiver.Companion.ACTION_STOP

class MusicService : Service() {
    private lateinit var player: MediaPlayer

    override fun onCreate() {
        super.onCreate()

        player =
            MediaPlayer().apply {
                setDataSource(resources.openRawResourceFd(R.raw.edm_deep_house))
                isLooping = true

                setOnPreparedListener {
                    Log.d("MusicService", "MediaPlayer prepared, starting now")
                    start()
                    Log.d("MusicService", "isPlaying after start = $isPlaying")
                    updateNotification()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e("MusicService", "MediaPlayer error: what=$what, extra=$extra")
                    true
                }

                prepareAsync()
            }

        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                if (!player.isPlaying) player.start()
                updateNotification()
            }

            ACTION_PAUSE -> {
                if (player.isPlaying) player.pause()
                updateNotification()
            }

            ACTION_STOP -> {
                stopSelf() // 👈 서비스 종료
            }

            else -> {
                startForeground(NOTIFICATION_ID, buildNotification())
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (::player.isInitialized) {
            player.stop()
            player.release()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH,
                )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun getActionPendingIntent(action: String): PendingIntent {
        val intent =
            Intent(this, MusicNotificationReceiver::class.java).apply {
                this.action = action
            }
        return PendingIntent.getBroadcast(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun updateNotification() {
        if (!::player.isInitialized) return

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val customView =
            RemoteViews(packageName, R.layout.custom_notification).apply {
                setTextViewText(R.id.tv_title, "음악 재생 중")
                setTextViewText(R.id.tv_desc, "EDM Deep House")

                val playPauseIcon =
                    if (player.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                val action = if (player.isPlaying) ACTION_PAUSE else ACTION_PLAY
                setImageViewResource(R.id.btn_play_pause, playPauseIcon)
                setOnClickPendingIntent(R.id.btn_play_pause, getActionPendingIntent(action))

                // ✅ 종료 버튼 동작 연결
                setOnClickPendingIntent(R.id.btn_stop_service, getActionPendingIntent(ACTION_STOP))
            }

        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setCustomContentView(customView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "music_channel"
        private const val CHANNEL_NAME = "Music Playback"
        private const val NOTIFICATION_ID = 1
    }
}
