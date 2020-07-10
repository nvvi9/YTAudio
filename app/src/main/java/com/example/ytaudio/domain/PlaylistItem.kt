package com.example.ytaudio.domain

import com.example.ytaudio.database.entities.AudioInfo


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
                    youtubeId, audioDetails.title, audioDetails.author,
                    thumbnails.maxBy { it.height }?.uri,
                    audioDetails.durationSeconds
                )
            }
    }
}