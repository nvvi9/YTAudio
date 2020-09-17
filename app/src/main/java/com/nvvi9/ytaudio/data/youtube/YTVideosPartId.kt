package com.nvvi9.ytaudio.data.youtube


data class YTVideosPartId(
    val kind: String,
    val etag: String,
    val items: List<YTPartIdItem>,
    val prevPageToken: String? = null,
    val nextPageToken: String? = null,
    val pageInfo: PageInfo
)

data class YTPartIdItem(
    val kind: String,
    val etag: String,
    val id: String
)