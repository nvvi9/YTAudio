package com.example.ytaudio.vo

import com.example.ytaudio.data.youtube.YTSearchItem
import com.example.ytaudio.data.youtube.YTVideosItem


data class YouTubeItem(
    val videoId: String,
    val title: String,
    val thumbnailUri: String,
    val channelTitle: String
) {

    companion object {

        fun from(ytSearchItem: YTSearchItem) =
            ytSearchItem.run {
                YouTubeItem(
                    id.videoId, snippet.title,
                    snippet.thumbnails.default.url,
                    snippet.channelTitle
                )
            }

        fun from(ytVideosItem: YTVideosItem) =
            ytVideosItem.run {
                YouTubeItem(id, snippet.title, snippet.thumbnails.high.url, snippet.channelTitle)
            }
    }
}