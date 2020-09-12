package com.example.ytaudio.vo

import com.example.ytaudio.data.streamyt.VideoDetails


data class YouTubeItem(
    val id: String,
    val title: String,
    val thumbnailUri: String?,
    val channelTitle: String,
    val viewCount: Long?,
    val durationSeconds: Long?,
    var isAdded: Boolean = false
)

fun VideoDetails.toYouTubeItem() =
    YouTubeItem(id, title ?: "", thumbnails[1].url, channel ?: "", viewCount, durationSeconds)
