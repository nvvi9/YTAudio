package com.nvvi9.ytaudio.data.ytstream

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class YTVideoDetailsRemoteKeys(
    @PrimaryKey val id: String,
    val prevPageToken: String?,
    val nextPageToken: String?
)