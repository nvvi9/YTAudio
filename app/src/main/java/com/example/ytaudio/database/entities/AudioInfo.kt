package com.example.ytaudio.database.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.ytaudio.domain.PlaylistItem
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData


@Entity(indices = [Index(value = ["youtubeId"], unique = true)])
data class AudioInfo(

    @PrimaryKey val youtubeId: String,
    val title: String,
    val author: String,
    val channelId: String,
    val description: String,
    val keywords: List<String>,
    val durationSeconds: Int,
    val thumbnails: List<Thumbnail>,
    var averageRating: Double,
    var viewCount: String,
    var audioStreams: List<AudioStream>,
    var nextUpdateTimeMillis: Long
) {

    val needUpdate: Boolean
        get() =
            System.currentTimeMillis() >= nextUpdateTimeMillis - 10

    fun toPlaylistItem() =
        PlaylistItem(
            id = youtubeId,
            title = title,
            author = author,
            thumbnailUri = thumbnails.minBy { it.height }?.uri,
            duration = durationSeconds
        )

    @Ignore
    constructor(data: YoutubeVideoData) : this(
        data.videoDetails.videoId, data.videoDetails.title,
        data.videoDetails.author, data.videoDetails.channelId,
        data.videoDetails.shortDescription, data.videoDetails.keywords,
        data.videoDetails.lengthSeconds.toIntOrNull() ?: 0,
        data.videoDetails.thumbnail.thumbnails.map { Thumbnail(it) },
        data.videoDetails.averageRating, data.videoDetails.viewCount,
        data.streamingData.adaptiveAudioStreams.map { AudioStream(it) },
        System.currentTimeMillis() +
                data.streamingData.expiresInSeconds.toLongOrNull()!!.times(1000)
    )
}