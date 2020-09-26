package com.nvvi9.ytaudio.data.audioinfo

import androidx.room.*
import com.nvvi9.model.Thumbnail
import com.nvvi9.model.VideoData
import com.nvvi9.model.streams.StreamType


@Entity(indices = [Index(value = ["id"], unique = true)])
data class AudioInfo(
    @PrimaryKey val id: String,
    @Embedded val details: AudioDetails,
    val thumbnails: List<Thumbnail>,
    val audioStreams: List<AudioStream>,
    val expiresInSeconds: Long?,
    val lastUpdateTimeSeconds: Long
) {

    val needUpdate
        get() = expiresInSeconds?.let {
            System.currentTimeMillis() / 1000 >= lastUpdateTimeSeconds + it - UPDATE_TIME_GAP
        }

    companion object {

        @Ignore
        private const val UPDATE_TIME_GAP = 10

        @Ignore
        fun fromVideoData(videoData: VideoData): AudioInfo? =
            with(videoData) {
                streams.filter { it.streamDetails.type == StreamType.AUDIO }
                    .mapNotNull { AudioStream.fromStream(it) }
                    .takeIf { it.isNotEmpty() }
                    ?.let { streams ->
                        videoDetails.run {
                            AudioInfo(
                                id, AudioDetails(title, channel ?: "", durationSeconds ?: 0),
                                thumbnails, streams, expiresInSeconds,
                                System.currentTimeMillis() / 1000
                            )
                        }
                    }
            }
    }
}