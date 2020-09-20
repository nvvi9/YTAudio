package com.nvvi9.ytaudio.ui.helper

import android.os.Handler
import android.os.Message
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.utils.extensions.currentPlayBackPosition
import com.nvvi9.ytaudio.utils.extensions.duration
import javax.inject.Inject


class AudioProgressViewUpdateHelper : Handler {

    @Inject
    lateinit var audioServiceConnection: AudioServiceConnection

    private var callback: Callback? = null
    private var intervalPlaying = 0L
    private var intervalPaused = 0L

    fun start() {
        queueNextRefresh(1)
    }

    fun stop() {
        removeMessages(CMD_REFRESH_PROGRESS_VIEWS)
    }

    constructor(callback: Callback) {
        this.callback = callback
        this.intervalPlaying = UPDATE_INTERVAL_PLAYING
        this.intervalPaused = UPDATE_INTERVAL_PAUSED
    }

    constructor(callback: Callback, intervalPlaying: Long, intervalPaused: Long) {
        this.callback = callback
        this.intervalPlaying = intervalPlaying
        this.intervalPaused = intervalPaused
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        if (msg.what == CMD_REFRESH_PROGRESS_VIEWS) {
            refreshProgressViews()?.let { queueNextRefresh(it) }
        }
    }

    private fun refreshProgressViews(): Long? {
        return audioServiceConnection.playbackState.value?.currentPlayBackPosition?.let { progressMillis ->
            audioServiceConnection.nowPlaying.value?.duration?.let { totalMillis ->
                if (totalMillis > 0)
                    callback?.onUpdateProgressViews(progressMillis, totalMillis)

                if (audioServiceConnection.isConnected.value == false) {
                    intervalPaused
                } else {
                    val remainingMillis = intervalPlaying - progressMillis % intervalPlaying
                    Math.max(MIN_INTERVAL, remainingMillis)
                }
            }
        }
    }

    private fun queueNextRefresh(delay: Long) {
        val message = obtainMessage(CMD_REFRESH_PROGRESS_VIEWS)
        removeMessages(CMD_REFRESH_PROGRESS_VIEWS)
        sendMessageDelayed(message, delay)
    }

    interface Callback {
        fun onUpdateProgressViews(progress: Long, total: Long)
    }

    companion object {
        private const val CMD_REFRESH_PROGRESS_VIEWS = 1
        private const val MIN_INTERVAL = 20L
        private const val UPDATE_INTERVAL_PLAYING = 1000L
        private const val UPDATE_INTERVAL_PAUSED = 500L
    }
}