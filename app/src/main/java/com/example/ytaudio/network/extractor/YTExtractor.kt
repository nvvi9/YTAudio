package com.example.ytaudio.network.extractor

import android.util.Log
import com.example.ytaudio.database.entities.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class YTExtractor : YoutubeJExtractor() {

    suspend fun extractAudioInfo(videoId: String, maxTries: Int = 3) =
        withContext(Dispatchers.IO) {
            var audioInfo: AudioInfo? = null
            for (i in 0 until maxTries) {
                try {
                    val youtubeVideoData = super.extract(videoId)

                    audioInfo =
                        if (!youtubeVideoData.videoDetails.isLiveContent) {
                            AudioInfo(youtubeVideoData)
                        } else {
                            null
                        }
                    break
                } catch (e: ExtractionException) {
                    Log.e(javaClass.simpleName, "$videoId extraction failed on ${i + 1} attempt")
                }
            }
            audioInfo
        }
}