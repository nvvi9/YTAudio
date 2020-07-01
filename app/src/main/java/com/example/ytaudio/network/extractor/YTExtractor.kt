package com.example.ytaudio.network.extractor

import com.example.ytaudio.database.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YTExtractor : YoutubeJExtractor() {

    suspend fun extractAudioInfo(videoId: String?) =
        withContext(Dispatchers.Default) {
            val youtubeVideoData = super.extract(videoId)
            if (youtubeVideoData.videoDetails.isLiveContent) {
                throw LiveContentException("Live content playback unavailable")
            }
            youtubeVideoData.asAudioInfo()
        }

    private fun YoutubeVideoData.asAudioInfo(): AudioInfo {
        val adaptiveAudioStream =
            streamingData.adaptiveAudioStreams.maxBy { it.averageBitrate }

        return AudioInfo(
            youtubeId = videoDetails.videoId,
            audioStreamingUri = adaptiveAudioStream!!.url,
            thumbnailUri = videoDetails.thumbnail.thumbnails.maxBy { it.height }!!.url,
            title = videoDetails.title,
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
            streamingUriActiveTimeSeconds = streamingData.expiresInSeconds.toLong()
        )
    }

    class LiveContentException(msg: String) : Exception(msg)
}
