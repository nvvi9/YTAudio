package com.example.ytaudio.network.youtube

import com.example.ytaudio.domain.SearchItem

data class YTResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String,
    val regionCode: String,
    val pageInfo: PageInfo,
    val items: List<VideoItem>
) {

    val searchItemList
        get() = items.map {
            SearchItem(
                it.id.videoId,
                it.snippet.title,
                it.snippet.thumbnails.default.url,
                it.snippet.channelTitle
            )
        }
}

data class PageInfo(
    val totalResults: Int,
    val resultsPerPage: Int
)

data class VideoItem(
    val kind: String,
    val etag: String,
    val id: Id,
    val snippet: Snippet
) {

    fun toSearchItem() =
        SearchItem(
            id.videoId, snippet.title,
            snippet.thumbnails.default.url,
            snippet.channelTitle
        )
}

data class Id(
    val kind: String,
    val videoId: String
)

data class Snippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String,
    val liveBroadcastContent: String,
    val publishTime: String
)

data class Thumbnails(
    val default: Thumbnail,
    val medium: Thumbnail,
    val high: Thumbnail
)

data class Thumbnail(
    val url: String,
    val width: Int,
    val height: Int
)