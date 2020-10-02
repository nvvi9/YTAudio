package com.nvvi9.ytaudio.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.nvvi9.ytaudio.domain.AudioInfoUseCases
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.utils.Event
import com.nvvi9.ytaudio.utils.extensions.id
import com.nvvi9.ytaudio.utils.extensions.isPlayEnabled
import com.nvvi9.ytaudio.utils.extensions.isPrepared
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
class MainViewModel @Inject constructor(
    private val audioInfoUseCases: AudioInfoUseCases,
    private val audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    private val addJob = Job()
    private val deleteJob = Job()

    private val addScope = CoroutineScope(Dispatchers.Main + addJob)
    private val deleteScope = CoroutineScope(Dispatchers.Main + deleteJob)

    val networkFailure: LiveData<Event<Boolean>>
        get() = audioServiceConnection.networkFailure
            .map { Event(it) }

    override fun onCleared() {
        super.onCleared()
        deleteJob.cancel()
        addJob.cancel()
    }

    fun audioItemClicked(audioId: String) {
        audioServiceConnection.run {
            if (playbackState.value?.isPrepared == true && playbackState.value?.isPlayEnabled == true && audioId == nowPlaying.value?.id) {
                transportControls.play()
            } else {
                transportControls.playFromMediaId(audioId, null)
            }
        }
    }

    fun addToPlaylist(id: String) {
        addScope.launch {
            try {
                audioInfoUseCases.addToPlaylist(id)
            } catch (t: Throwable) {
                Log.e(javaClass.simpleName, t.stackTraceToString())
            }
        }
    }

    fun deleteFromPlaylist(id: String) {
        deleteScope.launch {
            try {
                audioInfoUseCases.addToPlaylist(id)
            } catch (t: Throwable) {
                Log.e(javaClass.simpleName, t.stackTraceToString())
            }
        }
    }
}