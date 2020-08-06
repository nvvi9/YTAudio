package com.example.ytaudio.vo

import com.example.ytaudio.data.audioinfo.AudioInfo


data class PlaylistItem @JvmOverloads constructor(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailUri: String?,
    val duration: Int,
    val playbackState: Int = 0
) {

    companion object {

        fun from(audioInfo: AudioInfo) =
            audioInfo.run {
                PlaylistItem(
                    id, details.title, details.channel,
                    thumbnails.maxBy { it.height }?.url,
                    details.durationSeconds.toIntOrNull() ?: 0
                )
            }
    }
}