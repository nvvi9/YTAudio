package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.utils.Event
import com.nvvi9.ytaudio.utils.extensions.id
import com.nvvi9.ytaudio.utils.extensions.isPlayEnabled
import com.nvvi9.ytaudio.utils.extensions.isPrepared
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class MainViewModel @Inject constructor(
    private val audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    val networkFailure: LiveData<Event<Boolean>>
        get() = audioServiceConnection.networkFailure
            .map { Event(it) }

    fun audioItemClicked(audioId: String) {
        audioServiceConnection.run {
            if (playbackState.value?.isPrepared == true && playbackState.value?.isPlayEnabled == true && audioId == nowPlaying.value?.id) {
                transportControls.play()
            } else {
                transportControls.playFromMediaId(audioId, null)
            }
        }
    }
}