package com.example.ytaudio.data.streamyt

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class VideoDetails(
    @PrimaryKey val id: String,
    val title: String,
    val channel: String,
    val channelId: String,
    val description: String,
    val durationSeconds: Long,
    val viewCount: Long,
    val thumbnails: List<Thumbnail>,
    val expiresInSeconds: Long?,
    val isLiveStream: Boolean?
)