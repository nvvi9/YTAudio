package com.example.ytaudio.screens.url_link

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData
import kotlinx.coroutines.*

class SourceLinkViewModel(
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val extractor = YoutubeJExtractor()
    private val viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun onExtract(youtubeUrl: CharSequence) {
        val youtubeId = youtubeUrl.takeLastWhile { it != '=' && it != '/' }.toString()

        uiScope.launch {
            try {
                val videoData = getVideoData(youtubeId)

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

                startNavigation()
            } catch (e: ExtractionException) {
                showToast("Extraction failed")
            } catch (e: YoutubeRequestException) {
                showToast("Check your connection")
            } catch (e: Exception) {
                showToast("Unknown error")
            }
        }
    }

    private suspend fun getVideoData(youtubeId: String): YoutubeVideoData {
        return withContext(Dispatchers.IO) {
            extractor.extract(youtubeId)
        }
    }

    private fun showToast(message: String) =
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()


    private val _navigateToPlaylist = MutableLiveData(false)
    val navigateToPlaylist: LiveData<Boolean>
        get() = _navigateToPlaylist

    private fun startNavigation() {
        _navigateToPlaylist.value = true
    }

    fun navigationDone() {
        _navigateToPlaylist.value = false
    }
}