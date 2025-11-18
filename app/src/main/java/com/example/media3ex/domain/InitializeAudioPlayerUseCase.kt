package com.example.media3ex.domain

import androidx.media3.common.Player
import javax.inject.Inject

class InitializeAudioPlayerUseCase
    @Inject
    constructor(
        private val repository: AudioPlayerRepository,
    ) {
        operator fun invoke(
            mediaUri: String,
            onConnected: (Player) -> Unit,
        ) {
            repository.initialize { player ->
                repository.setMediaItem(mediaUri)
                onConnected(player)
            }
        }
    }
