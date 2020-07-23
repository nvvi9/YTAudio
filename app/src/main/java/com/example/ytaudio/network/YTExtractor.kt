package com.example.ytaudio.network

import com.example.ytaudio.data.audioinfo.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


class YTExtractor : YoutubeJExtractor() {

    suspend fun extractAudioInfoIO(id: String) =
        withContext(Dispatchers.IO) {
            getAudioInfo(id)
        }

    suspend fun extractAudioInfoAsync(id: String) =
        coroutineScope {
            async {
                getAudioInfo(id)
            }
        }

    suspend fun extractAudioInfo(id: String) =
        coroutineScope {
            getAudioInfo(id)
        }

    fun extractAudioInfoFlow(id: String) = flow {
        emit(getAudioInfo(id))
    }

    private fun getAudioInfo(id: String) =
        try {
            super.extract(id)
        } catch (t: Throwable) {
            null
        }?.let { AudioInfo.from(it) }
}