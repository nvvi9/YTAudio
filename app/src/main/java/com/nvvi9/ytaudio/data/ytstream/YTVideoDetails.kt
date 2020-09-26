package com.nvvi9.ytaudio.data.ytstream

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nvvi9.model.Thumbnail
import com.nvvi9.model.VideoDetails


@Entity
data class YTVideoDetails(
    @PrimaryKey val id: String,
    val title: String?,
    val channel: String?,
    val channelId: String?,
    val description: String?,
    val durationSeconds: Long?,
    val viewCount: Long?,
    val thumbnails: List<Thumbnail>,
    val expiresInSeconds: Long?,
    val isLiveStream: Boolean?
) {
    companion object {
        fun create(videoDetails: VideoDetails) =
            with(videoDetails) {
                YTVideoDetails(
                    id, title, channel, channelId, description, durationSeconds,
                    viewCount, thumbnails, expiresInSeconds, isLiveStream
                )
            }
    }
}