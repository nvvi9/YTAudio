package com.nvvi9.ytaudio.vo

import com.nvvi9.ytaudio.data.audioinfo.AudioInfo


data class PlaylistItem @JvmOverloads constructor(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailUri: String?,
    val duration: Long,
    val playbackState: Int = 0
) {

    companion object {

        fun from(audioInfo: AudioInfo) =
            audioInfo.run {
                PlaylistItem(
                    id, details.title, details.author,
                    thumbnails.getOrNull(1)?.url,
                    details.duration
                )
            }
    }
}