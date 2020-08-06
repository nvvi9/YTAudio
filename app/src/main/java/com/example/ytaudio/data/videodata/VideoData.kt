package com.example.ytaudio.data.videodata

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.ytaudio.data.streamyt.Thumbnail

@Entity
data class VideoData(
    @PrimaryKey override val id: String,
    @Embedded override val details: Details,
    override val thumbnails: List<Thumbnail>,
    override val audioStreams: List<AudioStream>,
    override val aliveTimeMillis: Long,
    override val lastUpdateTimeMillis: Long,
    val videoStreams: List<VideoStream>,
    val hlsUri: String?,
    val dashUri: String?
) : YTData {

    val needUpdate: Boolean
        get() = System.currentTimeMillis() > lastUpdateTimeMillis + aliveTimeMillis - UPDATE_TIME_GAP

    companion object {
        @Ignore
        private const val UPDATE_TIME_GAP = 10

//        fun create(
//            youtubeVideoData: YoutubeVideoData,
//            prevPageToken: String?,
//            nextPageToken: String?
//        ): VideoData = with(youtubeVideoData) {
//            val details = videoDetails.toDetails()
//            val thumbnails = videoDetails.thumbnail.thumbnails.toThumbnailList()
//            val audioStreams = streamingData.adaptiveAudioStreams.toAudioStreamList()
//            val videoStreams = streamingData.adaptiveVideoStreams.toVideoStreamList()
//
//            VideoData(
//                videoDetails.videoId, details, thumbnails, audioStreams,
//                streamingData.expiresInSeconds.toLong().times(1000),
//                System.currentTimeMillis(), videoStreams, streamingData.hlsManifestUrl,
//                streamingData.dashManifestUrl
//            )
//        }
    }

}