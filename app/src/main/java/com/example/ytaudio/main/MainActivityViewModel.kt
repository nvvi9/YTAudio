package com.example.ytaudio.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.repositories.AudioRepository
import com.example.ytaudio.service.MediaPlaybackServiceConnection
import com.example.ytaudio.service.extensions.id
import com.example.ytaudio.service.extensions.isPlayEnabled
import com.example.ytaudio.service.extensions.isPlaying
import com.example.ytaudio.service.extensions.isPrepared
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val repository: AudioRepository,
    private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection
) : ViewModel() {

    private val needUpdateObserver = Observer<List<AudioInfo>?> { list ->
        list?.filter { it.needUpdate }?.let {
            if (it.isNotEmpty()) {
                updateAudioInfo(it)
            }
        }
    }

    private fun updateAudioInfo(audioList: List<AudioInfo>) {
        viewModelScope.launch {
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
        databaseAudioInfo.removeObserver(needUpdateObserver)
        super.onCleared()
    }
}