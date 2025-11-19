package com.example.media3ex.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.media3ex.R
import com.example.media3ex.service.AudioPlaybackService.Companion.CHANNEL_ID
import com.example.media3ex.service.AudioPlaybackService.Companion.NOTIFICATION_ID
import com.google.common.collect.ImmutableList

@UnstableApi
class CustomMediaNotificationProvider(
    private val context: Context,
) : MediaNotification.Provider {
    override fun createNotification(
        mediaSession: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback,
    ): MediaNotification {
        val player = mediaSession.player
        val mediaMetadata = player.currentMediaItem?.mediaMetadata

        val title = mediaMetadata?.title?.toString() ?: "재생 중"
        val artist = mediaMetadata?.artist?.toString() ?: "알 수 없는 아티스트"
        val albumArt = getAlbumArt()

        val builder =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(title)
                .setContentText(artist)
                .setLargeIcon(albumArt)
                .setContentIntent(mediaSession.sessionActivity)

        // ✅ customLayout의 버튼들을 순서대로 추가
        customLayout.forEach { commandButton ->
            commandButton.icon.let { icon ->
                commandButton.sessionCommand?.let { sessionCommand ->
                    // SessionCommand를 처리하는 PendingIntent 생성
                    val intent =
                        Intent(context, AudioPlaybackService::class.java).apply {
                            action = sessionCommand.customAction
                        }
                    val pendingIntent =
                        PendingIntent.getService(
                            context,
                            sessionCommand.customAction.hashCode(),
                            intent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                        )

                    // 직접 Action 생성
                    val action =
                        NotificationCompat.Action
                            .Builder(
                                icon,
                                commandButton.displayName,
                                pendingIntent,
                            ).build()

                    builder.addAction(action)
                }
            }
        }

        val playPauseAction =
            if (player.isPlaying) {
                actionFactory.createMediaAction(
                    mediaSession,
                    IconCompat.createWithResource(context, R.drawable.ic_pause),
                    "일시정지",
                    Player.COMMAND_PLAY_PAUSE,
                )
            } else {
                actionFactory.createMediaAction(
                    mediaSession,
                    IconCompat.createWithResource(context, R.drawable.ic_play),
                    "재생",
                    Player.COMMAND_PLAY_PAUSE,
                )
            }
        builder.addAction(playPauseAction)

        // ✅ 총 액션 개수 계산 (customLayout + 재생/일시정지)
        val totalActions = customLayout.size + 1
        val compactViewIndices =
            when {
                totalActions >= 3 -> intArrayOf(0, 1, 2) // 10초뒤로, 재생/일시정지, 10초앞으로
                totalActions == 2 -> intArrayOf(0, 1)
                else -> intArrayOf(0)
            }

        builder
            .setStyle(
                MediaStyleNotificationHelper
                    .MediaStyle(mediaSession)
                    .setShowActionsInCompactView(*compactViewIndices),
            ).setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setOngoing(player.isPlaying)

        return MediaNotification(NOTIFICATION_ID, builder.build())
    }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: Bundle,
    ): Boolean = false

    private fun getAlbumArt(): Bitmap? =
        try {
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round)
        } catch (e: Exception) {
            null
        }
}
