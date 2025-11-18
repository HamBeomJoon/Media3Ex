package com.example.media3ex.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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

        val skipBackwardAction =
            actionFactory.createMediaAction(
                mediaSession,
                IconCompat.createWithResource(
                    context,
                    androidx.media3.session.R.drawable.media3_icon_skip_back_5,
                ),
                "5초 뒤로",
                Player.COMMAND_SEEK_BACK,
            )

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

        val skipForwardAction =
            actionFactory.createMediaAction(
                mediaSession,
                IconCompat.createWithResource(
                    context,
                    androidx.media3.session.R.drawable.media3_icon_skip_forward_15,
                ),
                "15초 앞으로",
                Player.COMMAND_SEEK_FORWARD,
            )

        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(title)
                .setContentText(artist)
                .setLargeIcon(albumArt)
                .setContentIntent(mediaSession.sessionActivity)
                .addAction(skipBackwardAction)
                .addAction(playPauseAction)
                .addAction(skipForwardAction)
                .setStyle(
                    MediaStyleNotificationHelper
                        .MediaStyle(mediaSession)
                        .setShowActionsInCompactView(0, 1, 2),
                ).setColor(ContextCompat.getColor(context, R.color.hearit_purple1))
                .setColorized(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setOngoing(player.isPlaying)
                .build()

        return MediaNotification(NOTIFICATION_ID, notification)
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
