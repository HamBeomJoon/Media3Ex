package com.example.media3ex.service

import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

@UnstableApi
class PlaybackSessionCallback : MediaSession.Callback {
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
    ): MediaSession.ConnectionResult {
        val baseResult = super.onConnect(session, controller)

        val sessionCommands =
            baseResult.availableSessionCommands
                .buildUpon()
                .add(SKIP_BACKWARD_COMMAND)
                .add(SKIP_FORWARD_COMMAND)
                .build()

        return MediaSession.ConnectionResult
            .AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .setAvailablePlayerCommands(baseResult.availablePlayerCommands)
            .build()
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle,
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            ACTION_SKIP_BACKWARD -> {
                val player = session.player
                val newPosition = (player.currentPosition - 5000).coerceAtLeast(0)
                player.seekTo(newPosition)
                return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }

            ACTION_SKIP_FORWARD -> {
                val player = session.player
                val newPosition = (player.currentPosition + 15000).coerceAtMost(player.duration)
                player.seekTo(newPosition)
                return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
        }
        return super.onCustomCommand(session, controller, customCommand, args)
    }

    companion object {
        const val ACTION_SKIP_BACKWARD = "SKIP_BACKWARD"
        const val ACTION_SKIP_FORWARD = "SKIP_FORWARD"

        val SKIP_BACKWARD_COMMAND = SessionCommand(ACTION_SKIP_BACKWARD, Bundle.EMPTY)
        val SKIP_FORWARD_COMMAND = SessionCommand(ACTION_SKIP_FORWARD, Bundle.EMPTY)
    }
}
