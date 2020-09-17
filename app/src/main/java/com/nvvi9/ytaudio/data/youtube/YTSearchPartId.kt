package com.nvvi9.ytaudio.data.youtube

data class YTSearchPartId(
    val kind: String,
    val etag: String,
    val nextPageToken: String?,
    val prevPageToken: String?,
    val regionCode: String,
    val pageInfo: PageInfo,
    val items: List<YTSearchPartIdItem>
)

data class YTSearchPartIdItem(
    val kind: String,
    val etag: String,
    val id: Id
)