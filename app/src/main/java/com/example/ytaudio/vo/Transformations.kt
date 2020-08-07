package com.example.ytaudio.vo

import com.example.ytaudio.data.audioinfo.AudioInfo

fun AudioInfo.toYouTubeItem() =
    YouTubeItem(
        id, details.title,
        thumbnails.maxBy { it.height }?.url,
        details.author
    )