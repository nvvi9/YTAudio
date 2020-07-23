package com.example.ytaudio.data.youtube

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class YTVideosRemoteKeys(
    @PrimaryKey val id: String,
    val prevPageToken: String?,
    val nextPageToken: String?
)