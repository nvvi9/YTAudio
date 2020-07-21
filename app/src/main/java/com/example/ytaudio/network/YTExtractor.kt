package com.example.ytaudio.network

import com.example.ytaudio.data.audioinfo.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext


class YTExtractor : YoutubeJExtractor() {

    suspend fun extractAudioInfo(videoId: String) =
        withContext(Dispatchers.IO) {
            try {
                super.extract(videoId)
            } catch (t: Throwable) {
                null
            }?.let { AudioInfo.from(it) }
        }

    suspend fun extractInfo(id: String) =
        coroutineScope {
            try {
                super.extract(id)
            } catch (t: Throwable) {
                null
            }?.let { AudioInfo.from(it) }
        }
}