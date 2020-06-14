package com.example.ytaudio.screens.audio_player

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.utils.Event
import com.example.ytaudio.utils.needUpdate
import com.example.ytaudio.utils.updateInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import kotlinx.coroutines.*

class AudioPlayerViewModel(
    val audioId: Long,
    val database: AudioDatabaseDao,
    application: Application
) :
    AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val extractor = object : YoutubeJExtractor() {
        suspend fun getVideoData(videoId: String?) =
            withContext(Dispatchers.IO) { super.extract(videoId) }
    }

//    private suspend fun AudioInfo.updateInfo() {
//        withContext(Dispatchers.IO) {
//            val videoData = extractor.extract(youtubeId)
//            val adaptiveAudioStream =
//                videoData.streamingData.adaptiveAudioStreams.maxBy { it.averageBitrate }
//            videoData.run {
//                audioUrl = adaptiveAudioStream!!.url
//                photoUrl = videoDetails.thumbnail.thumbnails.maxBy { it.height }!!.url
//                audioTitle = videoDetails.title
//                author = videoDetails.author
//                authorId = videoDetails.channelId
//                description = videoDetails.shortDescription
//                keywords = videoDetails.keywords.joinToString()
//                viewCount = videoDetails.viewCount.toIntOrNull() ?: 0
//                averageRating = videoDetails.averageRating
//                audioFormat = adaptiveAudioStream.extension
//                codec = adaptiveAudioStream.codec
//                bitrate = adaptiveAudioStream.bitrate
//                averageBitrate = adaptiveAudioStream.averageBitrate
//                audioDurationSeconds = videoDetails.lengthSeconds.toLong()
//                lastUpdateTimeSeconds = System.currentTimeMillis() / 1000
//                urlActiveTimeSeconds = streamingData.expiresInSeconds.toLong()
//            }
//        }
//    }

//    private fun AudioInfo.needUpdate() =
//        System.currentTimeMillis() > (lastUpdateTimeSeconds + urlActiveTimeSeconds - audioDurationSeconds * 2) * 1000

    private val _initializedAudioInfo = MutableLiveData<Event<AudioInfo>>()
    val initializedAudioInfo: LiveData<Event<AudioInfo>>
        get() = _initializedAudioInfo

    init {
        updateAudioInfo()
        updateDatabase()
    }

    private fun updateAudioInfo() {
        uiScope.launch {
            try {
                val audioInfo = database.get(audioId)
                if (audioInfo!!.needUpdate) {
                    audioInfo.updateInfo()
                    database.update(audioInfo)
                }
                _initializedAudioInfo.value = Event(audioInfo)
            } catch (e: ExtractionException) {
                showToast("Extraction failed")
            } catch (e: YoutubeRequestException) {
                showToast("Check your connection")
            } catch (e: Exception) {
                showToast("Unknown error")
            }
        }
    }

    private fun updateDatabase() {
        viewModelScope.launch {
            val startTimeMillis = System.currentTimeMillis()
            try {
                val audioInfoList = database.getAllAudioInfo()
                audioInfoList.forEach {
                    if (it.needUpdate && it.audioId != audioId) {
                        it.updateInfo()
                        database.update(it)
                    }
                }
                showToast(((System.currentTimeMillis() - startTimeMillis).toDouble() / 1000).toString())
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
        private val audioId: Long,
        private val dataSource: AudioDatabaseDao,
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AudioPlayerViewModel(audioId, dataSource, application) as T
        }
    }
}