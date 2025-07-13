package com.example.viewbindingex.musicService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class MusicNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val action = intent.action ?: return
        val serviceIntent =
            Intent(context, MusicService::class.java).apply {
                this.action = action
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    companion object {
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_STOP = "action_stop"
    }
}
