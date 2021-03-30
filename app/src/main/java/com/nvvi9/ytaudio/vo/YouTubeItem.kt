package com.nvvi9.ytaudio.vo

sealed class YouTubeItem {

    data class YouTubeVideoItem(
            val id: String,
            val title: String,
            val thumbnailUri: String?,
            val channelTitle: String,
            val viewCount: Long?,
            val durationSeconds: Long?,
            val inPlaylist: Boolean = false,
            var isAdded: Boolean = false
    ) : YouTubeItem()

    data class YouTubePlaylistItem(
            val playlistId: String,
            val title: String,
            val description: String,
            val thumbnailUri: String?,
            val channelTitle: String,

    ) : YouTubeItem()
}