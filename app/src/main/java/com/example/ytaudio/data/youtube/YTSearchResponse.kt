package com.example.ytaudio.data.youtube


data class YTSearchResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String? = null,
    val prevPageToken: String? = null,
    val regionCode: String,
    val pageInfo: PageInfo,
    val items: List<YTSearchItem>
)

data class PageInfo(
    val totalResults: Int,
    val resultsPerPage: Int
)

data class YTSearchItem(
    val kind: String,
    val etag: String,
    val id: Id,
    val snippet: YTSearchSnippet
)

data class Id(
    val kind: String,
    val videoId: String
)

data class YTSearchSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: YTSearchThumbnails,
    val channelTitle: String,
    val liveBroadcastContent: String,
    val publishTime: String
)

data class YTSearchThumbnails(
    val default: YTSearchThumbnail,
    val medium: YTSearchThumbnail,
    val high: YTSearchThumbnail
)

data class YTSearchThumbnail(
    val url: String,
    val width: Int,
    val height: Int
)