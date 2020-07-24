package com.example.ytaudio.vo

import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.data.youtube.YTSearchItem
import com.example.ytaudio.data.youtube.YTVideosItem

fun AudioInfo.toYouTubeItem() =
    YouTubeItem(
        youtubeId, audioDetails.title,
        thumbnails.maxBy { it.height }?.uri,
        audioDetails.author
    )

fun YTSearchItem.toYouTubeItem() =
    YouTubeItem(id.videoId, snippet.title, snippet.thumbnails.high.url, snippet.channelTitle)

fun YTVideosItem.toYouTubeItem() =
    YouTubeItem(
        id, snippet.title,
        snippet.thumbnails.run { maxres ?: standard ?: high }.url,
        snippet.channelTitle
    )