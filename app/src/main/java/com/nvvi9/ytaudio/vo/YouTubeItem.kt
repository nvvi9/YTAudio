package com.nvvi9.ytaudio.vo


data class YouTubeItem(
    val id: String,
    val title: String,
    val thumbnailUri: String?,
    val channelTitle: String,
    val viewCount: Long?,
    val durationSeconds: Long?,
    val inPlaylist: Boolean = false,
    var isAdded: Boolean = false
)