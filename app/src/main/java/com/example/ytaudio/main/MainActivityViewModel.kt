package com.example.ytaudio.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.repositories.AudioRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val repository: AudioRepository) : ViewModel() {

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
        repository.playAudio(audioId, false)
    }

    fun playAudio(audioId: String) {
        repository.playAudio(audioId, true)
    }

    override fun onCleared() {
        databaseAudioInfo.removeObserver(needUpdateObserver)
        super.onCleared()
    }
}