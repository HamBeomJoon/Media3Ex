package com.example.media3ex.domain

import javax.inject.Inject

class SetPlaybackSpeedUseCase
    @Inject
    constructor(
        private val repository: AudioPlayerRepository,
    ) {
        operator fun invoke(speed: Float) {
            repository.setPlaybackSpeed(speed)
        }
    }
