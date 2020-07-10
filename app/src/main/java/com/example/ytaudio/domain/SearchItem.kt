package com.example.ytaudio.domain

import com.example.ytaudio.network.youtube.VideoItem

data class SearchItem(
    val videoId: String,
    val title: String,
    val thumbnailUri: String,
    val channelTitle: String
) {

    companion object {

        fun from(videoItem: VideoItem) =
            videoItem.run {
                SearchItem(
                    id.videoId, snippet.title,
                    snippet.thumbnails.default.url,
                    snippet.channelTitle
                )
            }
    }
}