package com.example.ytaudio.data.streamyt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VideoDetailsRemoteKeys(
    @PrimaryKey val id: String,
    val prevPageToken: String?,
    val nextPageToken: String?
)