package com.example.ytaudio.data.audioinfo

import androidx.room.*
import com.example.ytaudio.data.streamyt.Thumbnail
import com.example.ytaudio.data.streamyt.VideoData


@Entity(indices = [Index(value = ["id"], unique = true)])
data class AudioInfo(
    @PrimaryKey val id: String,
    @Embedded val details: AudioDetails,
    val thumbnails: List<Thumbnail>,
    val audioStreams: List<AudioStream>,
    val expiresInSeconds: Long?,
    val lastUpdateTimeSeconds: Long
) {

    val needUpdate: Boolean?
        get() = expiresInSeconds?.let {
            System.currentTimeMillis() / 1000 >= lastUpdateTimeSeconds + it - UPDATE_TIME_GAP
        }

    companion object {
        @Ignore
        private const val UPDATE_TIME_GAP = 10

        @Ignore
        fun fromVideoData(videoData: VideoData): AudioInfo {
            return with(videoData) {
                val id = videoDetails.id
                val details = AudioDetails(
                    videoDetails.title,
                    videoDetails.channel,
                    videoDetails.durationSeconds
                )

                val thumbnails = videoDetails.thumbnails
                val streams = streams
                    .filter { it.streamDetails.type == "AUDIO" }
                    .map { AudioStream.fromStream(it) }.takeIf { it.isNotEmpty() }
                    ?: throw IllegalStateException("empty audio streams")

                val expiresInSeconds = videoDetails.expiresInSeconds

                AudioInfo(
                    id, details, thumbnails, streams,
                    expiresInSeconds, System.currentTimeMillis() / 1000
                )
            }
        }
    }
}