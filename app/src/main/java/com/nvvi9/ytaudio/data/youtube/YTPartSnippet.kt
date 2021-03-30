package com.nvvi9.ytaudio.data.youtube

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class YTPartSnippet(
        @Json(name = "etag") val etag: String,
        @Json(name = "items") val items: List<Item>,
        @Json(name = "kind") val kind: String,
        @Json(name = "nextPageToken") val nextPageToken: String?,
        @Json(name = "pageInfo") val pageInfo: PageInfo,
        @Json(name = "prevPageToken") val prevPageToken: String?,
        @Json(name = "regionCode") val regionCode: String
)

@JsonClass(generateAdapter = true)
data class Item(
        @Json(name = "etag") val etag: String,
        @Json(name = "id") val id: Id,
        @Json(name = "kind") val kind: String,
        @Json(name = "snippet") val snippet: Snippet
)

@JsonClass(generateAdapter = true)
data class Snippet(
        @Json(name = "channelId") val channelId: String,
        @Json(name = "channelTitle") val channelTitle: String,
        @Json(name = "description") val description: String,
        @Json(name = "liveBroadcastContent") val liveBroadcastContent: String,
        @Json(name = "publishTime") val publishTime: String,
        @Json(name = "publishedAt") val publishedAt: String,
        @Json(name = "thumbnails") val thumbnails: Thumbnails,
        @Json(name = "title") val title: String
)

@JsonClass(generateAdapter = true)
data class Thumbnails(
        @Json(name = "default") val default: Default,
        @Json(name = "high") val high: High,
        @Json(name = "medium") val medium: Medium,
        @Json(name = "standard") val standard: Standard?,
        @Json(name = "maxres") val maxres: Maxres?,
)

@JsonClass(generateAdapter = true)
data class Default(
        @Json(name = "height") val height: Int,
        @Json(name = "url") val url: String,
        @Json(name = "width") val width: Int
)

@JsonClass(generateAdapter = true)
data class High(
        @Json(name = "height") val height: Int,
        @Json(name = "url") val url: String,
        @Json(name = "width") val width: Int
)

@JsonClass(generateAdapter = true)
data class Medium(
        @Json(name = "height") val height: Int,
        @Json(name = "url") val url: String,
        @Json(name = "width") val width: Int
)

@JsonClass(generateAdapter = true)
data class Standard(
        @Json(name = "height") val height: Int,
        @Json(name = "url") val url: String,
        @Json(name = "width") val width: Int
)

@JsonClass(generateAdapter = true)
data class Maxres(
        @Json(name = "height") val height: Int,
        @Json(name = "url") val url: String,
        @Json(name = "width") val width: Int
)

@JsonClass(generateAdapter = true)
data class Localized(
        @Json(name = "title") val title: String,
        @Json(name = "description") val description: String,
        @Json(name = "defaultAudioLanguage") val defaultAudioLanguage: String
)