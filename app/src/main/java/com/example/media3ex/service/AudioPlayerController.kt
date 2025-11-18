package com.example.media3ex.service

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class AudioPlayerController(
    private val context: Context,
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    fun initialize(onConnected: (MediaController) -> Unit) {
        val sessionToken =
            SessionToken(
                context,
                ComponentName(context, AudioPlaybackService::class.java),
            )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                mediaController = controllerFuture?.get()
                mediaController?.let { onConnected(it) }
            },
            MoreExecutors.directExecutor(),
        )
    }

    fun setMediaItem(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
    }

    fun play() {
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun setPlaybackSpeed(speed: Float) {
        mediaController?.setPlaybackSpeed(speed)
    }

    fun addListener(listener: Player.Listener) {
        mediaController?.addListener(listener)
    }

    fun removeListener(listener: Player.Listener) {
        mediaController?.removeListener(listener)
    }

    fun getController(): MediaController? = mediaController

    fun release() {
        mediaController?.release()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        controllerFuture = null
        mediaController = null
    }
}
