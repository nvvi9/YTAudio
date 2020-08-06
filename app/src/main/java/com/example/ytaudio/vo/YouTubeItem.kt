package com.example.ytaudio.vo

import com.example.ytaudio.data.streamyt.VideoDetails
import com.example.ytaudio.data.videodata.YTData


data class YouTubeItem(
    val videoId: String,
    val title: String,
    val thumbnailUri: String?,
    val channelTitle: String
)

fun YTData.toYouTubeItem() =
    YouTubeItem(id, details.title, thumbnails.maxBy { it.height }?.url, details.channel)

fun VideoDetails.toYouTubeItem() =
    YouTubeItem(id, title, thumbnails[1].url, channel)
