package com.example.ytaudio.network

import com.example.ytaudio.data.audioinfo.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class YTExtractor : YoutubeJExtractor() {

    suspend fun extractAudioInfo(videoId: String) =
        withContext(Dispatchers.IO) {
            val youtubeVideoData = super.extract(videoId)
            AudioInfo.from(youtubeVideoData)
        }
}