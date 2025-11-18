package com.example.media3ex.domain

import javax.inject.Inject

class PauseAudioUseCase
    @Inject
    constructor(
        private val repository: AudioPlayerRepository,
    ) {
        operator fun invoke() {
            repository.pause()
        }
    }
