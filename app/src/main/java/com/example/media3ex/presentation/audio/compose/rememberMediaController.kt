package com.example.media3ex.presentation.audio.compose

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.media3ex.service.AudioPlaybackService
import com.google.common.util.concurrent.MoreExecutors

@UnstableApi
@Composable
fun rememberMediaController(): MediaController? {
    val context = LocalContext.current
    var controller by remember { mutableStateOf<MediaController?>(null) }

    DisposableEffect(context) {
        val sessionToken =
            SessionToken(
                context,
                ComponentName(context, AudioPlaybackService::class.java),
            )

        val controllerFuture =
            MediaController
                .Builder(context, sessionToken)
                .buildAsync()

        controllerFuture.addListener(
            {
                controller = controllerFuture.get()
            },
            MoreExecutors.directExecutor(),
        )

        onDispose {
            MediaController.releaseFuture(controllerFuture)
        }
    }

    return controller
}
