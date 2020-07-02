package com.example.ytaudio.network.extractor

import com.example.ytaudio.database.entities.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class YTExtractor : YoutubeJExtractor() {

    suspend fun extractAudioInfo(videoId: String?) =
        withContext(Dispatchers.IO) {
            val youtubeVideoData: YoutubeVideoData = super.extract(videoId)
            if (youtubeVideoData.videoDetails.isLiveContent) {
                throw LiveContentException("Live content playback unavailable")
            }
            AudioInfo(youtubeVideoData)
        }

    class LiveContentException(msg: String) : Exception(msg)
}