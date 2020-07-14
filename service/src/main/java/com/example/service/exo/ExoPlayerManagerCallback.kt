package com.example.service.exo

import com.example.service.domain.AudioItem

interface ExoPlayerManagerCallback {

    fun getCurrentAudioState(): Int

    fun isPlaying(): Boolean

    fun getCurrentStreamPosition(): Long

    fun getCurrentAudio(): AudioItem?

    fun updateLastStreamPosition()

    fun start()

    fun stop()

    fun play(audioItem: AudioItem)

    fun pause()

    fun seekTo(position: Long)

    fun setCallback(callback: AudioStateCallback)

    interface AudioStateCallback {

        fun onCompletion()

        fun onPlaybackStateChanged(state: Int)

        fun setCurrentPosition(position: Long, duration: Long)

        fun getCurrentAudio(): AudioItem?

        fun shuffle(isShuffle: Boolean)

        fun repeat(isRepeat: Boolean)

        fun repeatAll(isRepeatAll: Boolean)

        fun clearQueue()

        fun onError(error: String)
    }
}