package com.example.media3ex.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.media3ex.presentation.MainActivity
import com.example.media3ex.service.CustomMediaNotificationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object AudioServiceModule {
    @UnstableApi
    @Provides
    @ServiceScoped
    fun provideExoPlayer(
        @ApplicationContext context: Context,
    ): ExoPlayer =
        ExoPlayer
            .Builder(context)
            .setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true,
            ).setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                playWhenReady = false
            }

    @Provides
    @ServiceScoped
    fun provideSessionActivityPendingIntent(
        @ApplicationContext context: Context,
    ): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

    @UnstableApi
    @Provides
    @ServiceScoped
    fun provideCustomMediaNotificationProvider(
        @ApplicationContext context: Context,
    ): CustomMediaNotificationProvider = CustomMediaNotificationProvider(context)
}
