package com.example.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AudioService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object {

        const val ACTION_CMD = "ACTION_CMD"
        const val CMD_NAME = "CMD_NAME"
        const val CMD_PAUSE = "CMD_PAUSE"
        const val CMD_STOP_CASTING = "CMD_STOP_CASTING"
    }
}