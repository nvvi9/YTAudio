package com.example.ytaudio.vo

import com.example.ytaudio.data.videodata.YTData


data class YouTubeItem(
    val videoId: String,
    val title: String,
    val thumbnailUri: String?,
    val channelTitle: String
)

fun YTData.toYouTubeItem() =
    YouTubeItem(id, details.title, thumbnails.maxBy { it.height }?.uri, details.channel)