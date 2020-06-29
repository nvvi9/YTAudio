package com.example.ytaudio.activity

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.service.MediaPlaybackServiceConnection
import com.example.ytaudio.service.extensions.id
import com.example.ytaudio.service.extensions.isPlayEnabled
import com.example.ytaudio.service.extensions.isPlaying
import com.example.ytaudio.service.extensions.isPrepared
import com.example.ytaudio.utils.*
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val database: AudioDatabaseDao,
    private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection
) : ViewModel() {

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val needUpdateObserver = Observer<List<AudioInfo>?> { list ->
        list?.filter { it.needUpdate }?.let {
            if (it.isNotEmpty()) {
                updateAudioInfoList(it)
            }
        }
    }

    private val databaseAudioInfo = database.getAllAudio().apply {
        observeForever(needUpdateObserver)
    }

    fun audioItemClicked(audioItem: AudioInfo) {
        playAudio(audioItem, false)
    }

    private fun playAudio(audioItem: AudioInfo, pauseAllowed: Boolean) {
        val nowPlaying = mediaPlaybackServiceConnection.nowPlaying.value
        val transportControls = mediaPlaybackServiceConnection.transportControls

        val isPrepared = mediaPlaybackServiceConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && audioItem.audioId.toString() == nowPlaying?.id) {
            mediaPlaybackServiceConnection.playbackState.value?.let {
                when {
                    it.isPlaying -> if (pauseAllowed) transportControls.pause() else Unit
                    it.isPlayEnabled -> transportControls.play()
                }
            }
        } else {
            transportControls.playFromMediaId(audioItem.audioId.toString(), null)
        }
    }

    fun playAudio(audioId: String) {
        val nowPlaying = mediaPlaybackServiceConnection.nowPlaying.value
        val transportControls = mediaPlaybackServiceConnection.transportControls

        val isPrepared = mediaPlaybackServiceConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && audioId == nowPlaying?.id) {
            mediaPlaybackServiceConnection.playbackState.value?.let {
                when {
                    it.isPlaying -> transportControls.pause()
                    it.isPlayEnabled -> transportControls.play()
                }
            }
        } else {
            transportControls.playFromMediaId(audioId, null)
        }
    }

    private val nowUpdatingAudioInfoSet = mutableSetOf<AudioInfo>()

    private fun updateAudioInfoList(audioInfoList: List<AudioInfo>) {
        if (nowUpdatingAudioInfoSet.addAll(audioInfoList)) {
            coroutineScope.launch {
                val startTime = System.currentTimeMillis()
                var updatedSuccessfully = 0
                audioInfoList.forEachParallel {
                    try {
                        it.updateInfo()
                        updatedSuccessfully++
                    } catch (e: ExtractionException) {
                        Log.i(LOG_TAG, "${it.audioTitle} extraction failed")
                        database.delete(it)
                    } catch (e: YoutubeRequestException) {
                        Log.i(LOG_TAG, "network failure")
                    } catch (e: Exception) {
                        Log.i(LOG_TAG, e.toString())
                    }
                }

                database.update(audioInfoList)
                nowUpdatingAudioInfoSet.removeAll(audioInfoList)
                Log.i(
                    LOG_TAG,
                    "$updatedSuccessfully/${audioInfoList.size} items updated in ${System.currentTimeMillis() - startTime} ms"
                )
            }
        }
    }

    fun onExtract(youtubeUrl: String) {
        coroutineScope.launch {
            try {
                val startTime = System.currentTimeMillis()
                val audioInfo =
                    getAudioInfo(youtubeUrl.takeLastWhile { it != '=' && it != '/' })
                database.insert(audioInfo)
                Log.i(
                    LOG_TAG,
                    "${audioInfo.audioTitle} added in ${System.currentTimeMillis() - startTime} ms"
                )
                Log.i(LOG_TAG, audioInfo.toString())
            } catch (e: ExtractionException) {
                showToast("Extraction failed")
            } catch (e: YoutubeRequestException) {
                showToast("Check your connection")
            } catch (e: LiveContentException) {
                showToast(e.message)
            } catch (e: Exception) {
                showToast()
            }
        }
    }

    fun deleteAudioInfo(idList: List<Long>) {
        coroutineScope.launch {
            database.delete(idList)
        }
    }

    private fun showToast(message: String? = null) =
        Toast.makeText(
            mediaPlaybackServiceConnection.context,
            message ?: "Unknown error",
            Toast.LENGTH_SHORT
        ).show()

    override fun onCleared() {
        viewModelJob.cancel()
        databaseAudioInfo.removeObserver(needUpdateObserver)
        super.onCleared()
    }


    class Factory(
        private val databaseDao: AudioDatabaseDao,
        private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection
    ) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(databaseDao, mediaPlaybackServiceConnection) as T
        }
    }
}

private const val LOG_TAG = "MainActivityViewModel"