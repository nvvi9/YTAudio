package com.nvvi9.ytaudio.data.ytstream

import com.nvvi9.model.Thumbnail


data class YTVideoDetails(
    val id: String,
    val title: String?,
    val channel: String?,
    val channelId: String?,
    val description: String?,
    val durationSeconds: Long?,
    val viewCount: Long?,
    val thumbnails: List<Thumbnail>,
    val expiresInSeconds: Long?,
    val isLiveStream: Boolean?
)
