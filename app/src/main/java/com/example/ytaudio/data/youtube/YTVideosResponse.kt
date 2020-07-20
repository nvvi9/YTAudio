package com.example.ytaudio.data.youtube

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class YTVideosResponse(
    @PrimaryKey val etag: String,
    val kind: String,
    val items: List<YTVideosItem>,
    val nextPageToken: String? = null,
    val prevPageToken: String? = null,
    @Embedded val pageInfo: YTVideosPageInfo
)

data class YTVideosPageInfo(
    val totalResults: Int,
    val resultsPerPage: Int
)

data class YTVideosItem(
    val id: String,
    val kind: String,
    val etag: String,
    val snippet: YTVideosSnippet
)

data class YTVideosSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: YTVideosThumbnails,
    val channelTitle: String,
    val tags: List<String>? = null,
    val categoryId: String,
    val liveBroadcastContent: String,
    val localized: Localized? = null
)

data class YTVideosThumbnails(
    val default: YTVideosThumbnail,
    val medium: YTVideosThumbnail,
    val high: YTVideosThumbnail,
    val standard: YTVideosThumbnail? = null,
    val maxres: YTVideosThumbnail? = null
)

data class YTVideosThumbnail(
    val url: String,
    val width: Int,
    val height: Int
)

data class Localized(
    val title: String,
    val description: String,
    val defaultAudioLanguage: String? = null
)