package com.example.ytaudio.utils.extensions

import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.data.videodata.*
import com.github.kotvertolet.youtubejextractor.models.AdaptiveAudioStream
import com.github.kotvertolet.youtubejextractor.models.AdaptiveVideoStream
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.ThumbnailsItem
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.VideoDetails
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData


fun YoutubeVideoData.toVideoData(): VideoData {
    val details = videoDetails.toDetails()
    val thumbnails = videoDetails.thumbnail.thumbnails.toThumbnailList()
    val audioStreams = streamingData.adaptiveAudioStreams.toAudioStreamList()
    val videoStreams = streamingData.adaptiveVideoStreams.toVideoStreamList()

    return VideoData(
        videoDetails.videoId, details, thumbnails, audioStreams,
        streamingData.expiresInSeconds.toLong().times(1000),
        System.currentTimeMillis(), videoStreams, streamingData.hlsManifestUrl,
        streamingData.dashManifestUrl
    )
}

fun YoutubeVideoData.toAudioInfo(): AudioInfo {
    val details = videoDetails.toDetails()
    val thumbnails = videoDetails.thumbnail.thumbnails.toThumbnailList()
    val audioStreams = streamingData.adaptiveAudioStreams.toAudioStreamList()

    return AudioInfo(
        videoDetails.videoId, details, thumbnails, audioStreams,
        streamingData.expiresInSeconds.toLong().times(1000),
        System.currentTimeMillis()
    )
}

fun VideoDetails.toDetails() =
    Details(
        title, author, channelId, keywords,
        lengthSeconds, shortDescription,
        averageRating.toFloat(), viewCount
    )

fun ThumbnailsItem.toThumbnail() =
    Thumbnail(url, width, height)

fun AdaptiveAudioStream.toAudioStream() =
    AudioStream(
        url, codec, extension, getiTag(), bitrate,
        averageBitrate, audioSampleRate, audioChannels
    )

fun AdaptiveVideoStream.toVideoStream() =
    VideoStream(
        url, codec, extension, getiTag(), bitrate,
        averageBitrate, fps, size, qualityLabel, projectionType
    )

fun Iterable<ThumbnailsItem>.toThumbnailList() =
    map(ThumbnailsItem::toThumbnail)

fun Iterable<AdaptiveAudioStream>.toAudioStreamList() =
    map(AdaptiveAudioStream::toAudioStream)

fun Iterable<AdaptiveVideoStream>.toVideoStreamList() =
    map(AdaptiveVideoStream::toVideoStream)