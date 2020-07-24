package com.example.ytaudio.network

import com.example.ytaudio.utils.extensions.toVideoData
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


class YTExtractor : YoutubeJExtractor() {

    suspend fun extractVideoDataIO(id: String) =
        withContext(Dispatchers.IO) {
            getVideoData(id)
        }

    suspend fun extractVideoDataAsync(id: String) =
        coroutineScope {
            async {
                getVideoData(id)
            }
        }

    suspend fun extractVideoData(id: String) =
        coroutineScope {
            getVideoData(id)
        }

    fun extractVideoDataFlow(id: String) = flow {
        emit(getVideoData(id)?.toVideoData())
    }

    private fun getVideoData(id: String) =
        try {
            super.extract(id)
        } catch (t: Throwable) {
            null
        }
}