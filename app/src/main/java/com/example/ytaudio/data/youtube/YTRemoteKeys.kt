package com.example.ytaudio.data.youtube

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class YTRemoteKeys(
    @PrimaryKey
    val etag: String,
    val nextPageToken: String?
)