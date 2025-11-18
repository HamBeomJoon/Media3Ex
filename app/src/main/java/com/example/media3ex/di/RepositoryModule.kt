package com.example.media3ex.di

import com.example.media3ex.data.AudioPlayerRepositoryImpl
import com.example.media3ex.domain.AudioPlayerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAudioPlayerRepository(audioPlayerRepositoryImpl: AudioPlayerRepositoryImpl): AudioPlayerRepository
}
