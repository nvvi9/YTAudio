package com.example.ytaudio.domain


data class PlaylistItem(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailUri: String?,
    val duration: Int,
    val playbackState: Int = 0
)