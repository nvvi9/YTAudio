package com.example.ytaudio.screens.url_link

import android.app.Application
import android.util.Log
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
                Log.i("SourceLinkViewModel", videoData.toString())

            } catch (e: ExtractionException) {
                showToast("Extraction failed")
            } catch (e: YoutubeRequestException) {
                showToast("Check your connection")
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


    fun navigationDone() {
        _navigateToPlaylist.value = false
    }


    private suspend fun insert(audio: AudioInfo) {
        withContext(Dispatchers.IO) {
            database.insert(audio)
        }
    }
}