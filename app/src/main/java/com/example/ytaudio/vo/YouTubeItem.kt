package com.example.ytaudio.vo

import com.example.ytaudio.data.streamyt.VideoDetails


data class YouTubeItem(
    val videoId: String,
    val title: String,
    val thumbnailUri: String?,
    val channelTitle: String,
    val isAdded: Boolean = false
)

fun VideoDetails.toYouTubeItem() =
    YouTubeItem(id, title, thumbnails[1].url, channel)
