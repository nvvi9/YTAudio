package com.example.ytaudio.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.repositories.AudioRepository
import com.example.ytaudio.service.MediaPlaybackServiceConnection
import com.example.ytaudio.service.extensions.id
import com.example.ytaudio.service.extensions.isPlayEnabled
import com.example.ytaudio.service.extensions.isPlaying
import com.example.ytaudio.service.extensions.isPrepared
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivityViewModel(
    application: Application,
    private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection
) : AndroidViewModel(application) {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private val repository = AudioRepository(application)

    private val needUpdateObserver = Observer<List<AudioInfo>?> { list ->
        list?.filter { it.needUpdate }?.let {
            if (it.isNotEmpty()) {
                updateAudioInfo(it)
            }
        }
    }

    init {
        coroutineScope.launch {
            repository.updateAllAudioInfo()
        }
    }

    private fun updateAudioInfo(audioList: List<AudioInfo>) {
        coroutineScope.launch {
            repository.updateAudioInfoList(audioList)
        }
    }

    private val databaseAudioInfo = repository.audioInfoList.apply {
        observeForever(needUpdateObserver)
    }

    fun audioItemClicked(audioId: String) {
        playAudio(audioId, false)
    }

    private fun playAudio(audioId: String, pauseAllowed: Boolean) {
        val nowPlaying = mediaPlaybackServiceConnection.nowPlaying.value
        val transportControls = mediaPlaybackServiceConnection.transportControls

        val isPrepared = mediaPlaybackServiceConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && audioId == nowPlaying?.id) {
            mediaPlaybackServiceConnection.playbackState.value?.let {
                when {
                    it.isPlaying -> if (pauseAllowed) transportControls.pause() else Unit
                    it.isPlayEnabled -> transportControls.play()
                }
            }
        } else {
            transportControls.playFromMediaId(audioId, null)
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

    override fun onCleared() {
        job.cancel()
        databaseAudioInfo.removeObserver(needUpdateObserver)
        super.onCleared()
    }

    class Factory(
        private val application: Application,
        private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection
    ) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(application, mediaPlaybackServiceConnection) as T
        }
    }
}