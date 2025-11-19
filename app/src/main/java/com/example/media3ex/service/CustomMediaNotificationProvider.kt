package com.example.media3ex.service

import android.content.Context
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

        // 1. 10초 뒤로
        val skipBackwardButton =
            customLayout.find {
                it.sessionCommand?.customAction == PlaybackSessionCallback.ACTION_SKIP_BACKWARD
            }
        val skipForwardButton =
            customLayout.find {
                it.sessionCommand?.customAction == PlaybackSessionCallback.ACTION_SKIP_FORWARD
            }
        skipBackwardButton?.let { button ->
            // ⭐ actionFactory를 사용해서 SessionCommand 기반 액션 생성
            val action =
                actionFactory.createCustomAction(
                    mediaSession,
                    IconCompat.createWithResource(context, R.drawable.ic_back_ten),
                    button.displayName,
                    button.sessionCommand!!.customAction,
                    Bundle.EMPTY,
                )
            builder.addAction(action)
        }

        // 버튼 2: 재생/일시정지
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

        // 버튼 3: 10초 앞으로
        skipForwardButton?.let { button ->
            val action =
                actionFactory.createCustomAction(
                    mediaSession,
                    IconCompat.createWithResource(context, R.drawable.ic_forward_ten),
                    button.displayName,
                    button.sessionCommand!!.customAction,
                    Bundle.EMPTY,
                )
            builder.addAction(action)
        }

        builder
            .setStyle(
                MediaStyleNotificationHelper
                    .MediaStyle(mediaSession)
                    .setShowActionsInCompactView(0, 1, 2),
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
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_logo)
        } catch (e: Exception) {
            null
        }
}
