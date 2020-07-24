package com.example.ytaudio.vo


data class YouTubeItem(
    val videoId: String,
    val title: String,
    val thumbnailUri: String?,
    val channelTitle: String
)