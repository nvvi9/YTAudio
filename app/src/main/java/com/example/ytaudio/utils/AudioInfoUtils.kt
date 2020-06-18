package com.example.ytaudio.utils

import com.example.ytaudio.database.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


val AudioInfo.needUpdate: Boolean
    get() = System.currentTimeMillis() >= (lastUpdateTimeSeconds + urlActiveTimeSeconds - audioDurationSeconds * 2) * 1000

suspend fun AudioInfo.updateInfo() {
    withContext(Dispatchers.IO) {
        val extractor = YoutubeJExtractor()
        val videoData = extractor.extract(youtubeId)
        val adaptiveAudioStream =
            videoData.streamingData.adaptiveAudioStreams.maxBy { it.averageBitrate }
        videoData.run {
            audioUrl = adaptiveAudioStream!!.url
            photoUrl = videoDetails.thumbnail.thumbnails.maxBy { it.height }!!.url
            audioTitle = videoDetails.title
            author = videoDetails.author
            authorId = videoDetails.channelId
            description = videoDetails.shortDescription
            keywords = videoDetails.keywords.joinToString()
            viewCount = videoDetails.viewCount.toIntOrNull() ?: 0
            averageRating = videoDetails.averageRating
            audioFormat = adaptiveAudioStream.extension
            codec = adaptiveAudioStream.codec
            bitrate = adaptiveAudioStream.bitrate
            averageBitrate = adaptiveAudioStream.averageBitrate
            audioDurationSeconds = videoDetails.lengthSeconds.toLong()
            lastUpdateTimeSeconds = System.currentTimeMillis() / 1000
            urlActiveTimeSeconds = streamingData.expiresInSeconds.toLong()
        }
    }
}

suspend fun getAudioInfo(youtubeId: String) = withContext(Dispatchers.IO) {
    val extractor = YoutubeJExtractor()
    val videoData = extractor.extract(youtubeId)

    if (videoData.videoDetails.isLiveContent) {
        throw LiveContentException("Live content playback unavailable")
    }

    val adaptiveAudioStream = videoData.streamingData.adaptiveAudioStreams
        .maxBy { it.averageBitrate }

    videoData.run {
        AudioInfo(
            youtubeId = videoDetails.videoId,
            audioUrl = adaptiveAudioStream!!.url,
            photoUrl = videoDetails.thumbnail.thumbnails.maxBy { it.height }!!.url,
            audioTitle = videoDetails.title,
            author = videoDetails.author,
            authorId = videoDetails.channelId,
            description = videoDetails.shortDescription,
            keywords = videoDetails.keywords.joinToString(),
            viewCount = videoDetails.viewCount.toIntOrNull() ?: 0,
            averageRating = videoDetails.averageRating,
            audioFormat = adaptiveAudioStream.extension,
            codec = adaptiveAudioStream.codec,
            bitrate = adaptiveAudioStream.bitrate,
            averageBitrate = adaptiveAudioStream.averageBitrate,
            audioDurationSeconds = videoDetails.lengthSeconds.toLong(),
            lastUpdateTimeSeconds = System.currentTimeMillis() / 1000,
            urlActiveTimeSeconds = streamingData.expiresInSeconds.toLong()
        )
    }
}

fun <T> Collection<T>.forEachParallel(task: suspend (T) -> Unit) = runBlocking {
    map {
        async {
            task
        }
    }.forEach { it.await() }
}

class LiveContentException(msg: String) : Exception(msg)