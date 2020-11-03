package com.nvvi9.ytaudio.data.youtube

data class YTPlaylistItems(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val nextPageToken: String?,
    val pageInfo: PageInfo,
    val prevPageToken: String?
)

data class Item(
    val contentDetails: ContentDetails,
    val etag: String,
    val id: String,
    val kind: String
)

data class ContentDetails(
    val videoId: String,
    val videoPublishedAt: String
)