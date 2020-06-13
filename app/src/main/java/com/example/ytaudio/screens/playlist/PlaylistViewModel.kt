package com.example.ytaudio.screens.playlist

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData
import kotlinx.coroutines.*

class PlaylistViewModel(
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val extractor = object : YoutubeJExtractor() {
        suspend fun getVideoData(videoId: String?): YoutubeVideoData {
            return withContext(Dispatchers.IO) {
                super.extract(videoId)
            }
        }
    }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val audioPlaylist = database.getAllAudio()

    fun onExtract(youtubeUrl: String) {
        val youtubeId = youtubeUrl.takeLastWhile { it != '=' && it != '/' }

        uiScope.launch {
            try {
                val videoData = extractor.getVideoData(youtubeId)

                if (videoData.videoDetails.isLiveContent) {
                    showToast("live content")
                    return@launch
                }

                val adaptiveAudioStream = videoData.streamingData.adaptiveAudioStreams.maxBy {
                    it.averageBitrate
                }

                database.insert(videoData.run {
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
                })
            } catch (e: ExtractionException) {
                showToast("Extraction failed")
            } catch (e: YoutubeRequestException) {
                showToast("Check your connection")
            } catch (e: Exception) {
                showToast("Unknown error")
            }
        }
    }


    private fun showToast(message: String) =
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()

    class Factory(
        private val dataSource: AudioDatabaseDao,
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PlaylistViewModel(dataSource, application) as T
        }
    }
}