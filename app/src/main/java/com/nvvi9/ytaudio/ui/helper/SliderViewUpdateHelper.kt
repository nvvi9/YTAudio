package com.nvvi9.ytaudio.ui.helper

import android.os.Handler
import android.os.Message
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.utils.extensions.currentPlayBackPosition
import com.nvvi9.ytaudio.utils.extensions.duration
import javax.inject.Inject


class SliderViewUpdateHelper(
    private val sliderUpdateCallback: SliderUpdateCallback,
    private val intervalPlaying: Int = 0,
    private val intervalPaused: Int = 0
) : Handler() {

    @Inject
    lateinit var audioServiceConnection: AudioServiceConnection

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when (msg.what) {
            REFRESH_PROGRESS_VIEWS -> refreshSlideProgress()?.let { nextRefresh(it) }
        }
    }

    private fun refreshSlideProgress(): Long? {
        val currentProgress = audioServiceConnection.playbackState.value?.currentPlayBackPosition
        val total = audioServiceConnection.nowPlaying.value?.duration

        if (currentProgress != null && total != null) {
            if (total > 0) {
                sliderUpdateCallback.onUpdateProgress(currentProgress, total)
            }

            if (audioServiceConnection.playbackState.value != null) {
                return intervalPaused.toLong()
            }

            val remainingMillis = intervalPlaying - currentProgress % intervalPlaying

            return Math.max(MIN_INTERVAL, remainingMillis)
        }

        return null
    }

    private fun nextRefresh(delay: Long) {
        val msg = obtainMessage(REFRESH_PROGRESS_VIEWS)
        removeMessages(REFRESH_PROGRESS_VIEWS)
        sendMessageDelayed(msg, delay)
    }

    interface SliderUpdateCallback {
        fun onUpdateProgress(progress: Long, total: Long)
    }

    companion object {
        private const val REFRESH_PROGRESS_VIEWS = 1
        private const val MIN_INTERVAL = 20L
    }
}