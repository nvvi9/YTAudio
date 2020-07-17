package com.example.ytaudio.repositories

import android.util.Log
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.network.extractor.YTExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


open class BaseRepository(private val ytExtractor: YTExtractor) {

    protected suspend fun extractAudioInfo(id: String): AudioInfo? =
        withContext(Dispatchers.IO) {
            try {
                ytExtractor.extractAudioInfo(id)
            } catch (e: Exception) {
                Log.e(javaClass.simpleName, e.toString())
                null
            }
        }
}