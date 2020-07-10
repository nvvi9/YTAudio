package com.example.ytaudio.network.extractor

import com.example.ytaudio.database.entities.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class YTExtractor : YoutubeJExtractor() {

    suspend fun extractAudioInfo(videoId: String, maxTries: Int = 3) =
        withContext(Dispatchers.IO) {
            val youtubeVideoData = super.extract(videoId)
            AudioInfo.from(youtubeVideoData)
        }
}