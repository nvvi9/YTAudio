package com.nvvi9.ytaudio.vo

import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails


data class YouTubeItem(
    val id: String,
    val title: String,
    val thumbnailUri: String?,
    val channelTitle: String,
    val viewCount: Long?,
    val durationSeconds: Long?,
    var isAdded: Boolean = false
)

fun YTVideoDetails.toYouTubeItem() =
    YouTubeItem(id, title ?: "", thumbnails[1].url, channel ?: "", viewCount, durationSeconds)
