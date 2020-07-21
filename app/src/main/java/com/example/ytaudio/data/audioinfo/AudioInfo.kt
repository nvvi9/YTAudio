package com.example.ytaudio.data.audioinfo

import androidx.room.*
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData


@Entity(indices = [Index(value = ["youtubeId"], unique = true)])
data class AudioInfo(
    @PrimaryKey val youtubeId: String,
    @Embedded val audioDetails: AudioDetails,
    val thumbnails: List<Thumbnail>,
    val audioStreams: List<AudioStream>,
    val nextUpdateTimeMillis: Long
) {

    val needUpdate: Boolean
        get() =
            System.currentTimeMillis() >= nextUpdateTimeMillis - 10


    companion object {
        @Ignore
        fun from(data: YoutubeVideoData): AudioInfo =
            data.run {
                val audioDetails =
                    AudioDetails.from(videoDetails)
                val thumbnails =
                    videoDetails.thumbnail.thumbnails
                        .map { Thumbnail.from(it) }
                val audioStreams =
                    streamingData.adaptiveAudioStreams
                        .map { AudioStream.from(it) }

                AudioInfo(
                    videoDetails.videoId, audioDetails, thumbnails, audioStreams,
                    System.currentTimeMillis() + streamingData.expiresInSeconds.toLong().times(1000)
                )
            }
    }
}