package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvvi9.ytaudio.domain.AudioInfoUseCase
import com.nvvi9.ytaudio.utils.Event
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(
    private val audioInfoUseCase: AudioInfoUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    fun addToPlaylist(id: String) {
        viewModelScope.launch(ioDispatcher) {
            if (!audioInfoUseCase.addToPlaylist(id)) {
                _errorEvent.postValue(Event("Can't add to playlist"))
            }
        }
    }
}