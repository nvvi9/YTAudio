package com.nvvi9.ytaudio.vo


data class PlaylistItem(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailUri: String?,
    val duration: Long,
    var isPlayingNow: Boolean = false
)