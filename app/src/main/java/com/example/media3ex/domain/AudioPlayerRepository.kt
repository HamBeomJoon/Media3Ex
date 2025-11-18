package com.example.media3ex.domain

import androidx.media3.common.Player

interface AudioPlayerRepository {
    fun initialize(onConnected: (Player) -> Unit)

    fun setMediaItem(
        uri: String,
        title: String,
        artist: String,
        artworkUri: String? = null,
    )

    fun play()

    fun pause()

    fun setPlaybackSpeed(speed: Float)

    fun addListener(listener: Player.Listener)

    fun removeListener(listener: Player.Listener)

    fun getPlayer(): Player?

    fun release()
}
