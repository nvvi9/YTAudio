package com.nvvi9.ytaudio.vo

import android.support.v4.media.session.PlaybackStateCompat
import com.nvvi9.ytaudio.utils.Constants


class PlaybackState(
    val position: Int = 0,
    val shuffleMode: Int = 0,
    val repeatMode: Int = 0,
    val state: Int = 0
) {

    override fun toString() =
        "PlaybackState(position=${position}"

    companion object {
        fun fromPlaybackStateCompat(playbackState: PlaybackStateCompat) =
            with(playbackState.extras) {
                PlaybackState(
                    playbackState.position.toInt(),
                    playbackState.state,
                    this?.getInt(Constants.REPEAT_MODE) ?: PlaybackStateCompat.REPEAT_MODE_ONE,
                    this?.getInt(Constants.SHUFFLE_MODE) ?: PlaybackStateCompat.REPEAT_MODE_ALL
                )
            }
    }
}