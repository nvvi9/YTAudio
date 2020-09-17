package com.nvvi9.ytaudio.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvvi9.ytaudio.repositories.AudioInfoRepository
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.utils.Event
import com.nvvi9.ytaudio.utils.extensions.id
import com.nvvi9.ytaudio.utils.extensions.isPlayEnabled
import com.nvvi9.ytaudio.utils.extensions.isPlaying
import com.nvvi9.ytaudio.utils.extensions.isPrepared
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
class MainActivityViewModel @Inject constructor(
    private val audioInfoRepository: AudioInfoRepository,
    private val audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    private val _replaceEvent = MutableLiveData<Event<String>>()
    val replaceEvent: LiveData<Event<String>>
        get() = _replaceEvent

    fun audioItemClicked(audioId: String) {
        playAudio(audioId, false)
        _replaceEvent.value = Event(audioId)
    }

    private fun playAudio(audioId: String, pauseAllowed: Boolean) {
        val nowPlaying = audioServiceConnection.nowPlaying.value
        val transportControls = audioServiceConnection.transportControls

        val isPrepared = audioServiceConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && audioId == nowPlaying?.id) {
            audioServiceConnection.playbackState.value?.let {
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
        val nowPlaying = audioServiceConnection.nowPlaying.value
        val transportControls = audioServiceConnection.transportControls

        val isPrepared = audioServiceConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && audioId == nowPlaying?.id) {
            audioServiceConnection.playbackState.value?.let {
                when {
                    it.isPlaying -> transportControls.pause()
                    it.isPlayEnabled -> transportControls.play()
                }
            }
        } else {
            transportControls.playFromMediaId(audioId, null)
        }
    }

    fun addToPlaylist(id: String) {
        viewModelScope.launch {
            try {
                audioInfoRepository.insertIntoDatabase(id)
            } catch (t: Throwable) {
                Log.e(javaClass.simpleName, t.toString())
            }
        }
    }
}