package com.example.ytaudio.data.videodata

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class VideoDataRemoteKeys(
    @PrimaryKey val id: String,
    val prevPageToken: String?,
    val nextPageToken: String?
)