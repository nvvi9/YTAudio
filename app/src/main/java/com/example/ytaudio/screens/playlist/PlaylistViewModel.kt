package com.example.ytaudio.screens.playlist

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.utils.LiveContentException
import com.example.ytaudio.utils.getAudioInfo
import com.example.ytaudio.utils.needUpdate
import com.example.ytaudio.utils.updateInfo
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistViewModel(
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val audioPlaylist = database.getAllAudio()

    fun onExtract(youtubeUrl: String) {
        val youtubeId = youtubeUrl.takeLastWhile { it != '=' && it != '/' }

        uiScope.launch {
            try {
                val audioInfo = getAudioInfo(youtubeId)

                database.insert(audioInfo)
            } catch (e: ExtractionException) {
                showToast("Extraction failed")
            } catch (e: YoutubeRequestException) {
                showToast("Check your connection")
            } catch (e: LiveContentException) {
                showToast(e.message)
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
                    if (it.needUpdate) {
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

    init {
        updateDatabase()
    }

    private fun showToast(message: String?) =
        Toast.makeText(getApplication(), message ?: "error", Toast.LENGTH_SHORT).show()


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