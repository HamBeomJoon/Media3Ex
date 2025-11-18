package com.example.media3ex.data

import android.content.ComponentName
import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.media3ex.domain.AudioPlayerRepository
import com.example.media3ex.service.AudioPlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : AudioPlayerRepository {
        private var controllerFuture: ListenableFuture<MediaController>? = null
        private var mediaController: MediaController? = null

        override fun initialize(onConnected: (Player) -> Unit) {
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

        override fun setMediaItem(
            uri: String,
            title: String,
            artist: String,
            artworkUri: String?,
        ) {
            val metadata =
                MediaMetadata
                    .Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .apply {
                        artworkUri?.let { setArtworkUri(it.toUri()) }
                    }.build()

            val mediaItem =
                MediaItem
                    .Builder()
                    .setUri(uri)
                    .setMediaMetadata(metadata)
                    .build()

            mediaController?.setMediaItem(mediaItem)
            mediaController?.prepare()
        }

        override fun play() {
            mediaController?.play()
        }

        override fun pause() {
            mediaController?.pause()
        }

        override fun setPlaybackSpeed(speed: Float) {
            mediaController?.setPlaybackSpeed(speed)
        }

        override fun addListener(listener: Player.Listener) {
            mediaController?.addListener(listener)
        }

        override fun removeListener(listener: Player.Listener) {
            mediaController?.removeListener(listener)
        }

        override fun getPlayer(): Player? = mediaController

        override fun release() {
            mediaController?.release()
            controllerFuture?.let {
                MediaController.releaseFuture(it)
            }
            controllerFuture = null
            mediaController = null
        }
    }
