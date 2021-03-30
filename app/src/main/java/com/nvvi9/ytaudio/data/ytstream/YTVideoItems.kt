package com.nvvi9.ytaudio.data.ytstream

import com.nvvi9.model.Thumbnail

sealed class YTVideoItems {

    data class YTPlaylist(
            val playlistId: String,
            val title: String,
            val channelId: String,
            val channelTitle: String,
            val description: String,
            val thumbnails: List<Thumbnail>,
            val publishedAt: String,
    ) : YTVideoItems()

    data class YTVideo(
            val videoId: String,
            val title: String?,
            val channelTitle: String?,
            val channelId: String?,
            val description: String?,
            val durationSeconds: Long?,
            val viewCount: Long?,
            val thumbnails: List<Thumbnail>,
            val expiresInSeconds: Long?,
            val isLiveStream: Boolean?
    ) : YTVideoItems()
}