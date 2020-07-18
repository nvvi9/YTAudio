package com.example.ytaudio.data.audioinfo

import androidx.room.*
import com.example.ytaudio.utils.LiveContentException
import com.example.ytaudio.utils.UriAliveTimeMissException
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData


@Entity(indices = [Index(value = ["youtubeId"], unique = true)])
data class AudioInfo(
    @PrimaryKey val youtubeId: String,
    @Embedded val audioDetails: AudioDetails,
    val thumbnails: List<Thumbnail>,
    var audioStreams: List<AudioStream>,
    var nextUpdateTimeMillis: Long
) {

    val needUpdate: Boolean
        get() =
            System.currentTimeMillis() >= nextUpdateTimeMillis - 10

    companion object {
        @Ignore
        fun from(data: YoutubeVideoData): AudioInfo =
            data.run {
                if (videoDetails.isLiveContent) {
                    throw LiveContentException("can't play live content")
                }
                if (streamingData.expiresInSeconds == null) {
                    throw UriAliveTimeMissException("expires in seconds is null")
                }
                val audioDetails =
                    AudioDetails.from(
                        videoDetails
                    )
                val thumbnails =
                    videoDetails.thumbnail.thumbnails.map {
                        Thumbnail.from(
                            it
                        )
                    }
                val audioStreams =
                    streamingData.adaptiveAudioStreams.map {
                        AudioStream.from(
                            it
                        )
                    }

                AudioInfo(
                    videoDetails.videoId, audioDetails, thumbnails, audioStreams,
                    System.currentTimeMillis() + streamingData.expiresInSeconds.toLong().times(1000)
                )
            }
    }
}