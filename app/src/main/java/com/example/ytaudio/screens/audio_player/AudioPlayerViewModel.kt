package com.example.ytaudio.screens.audio_player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import kotlinx.coroutines.*

class AudioPlayerViewModel(
    val database: AudioDatabaseDao,
    application: Application
) :
    AndroidViewModel(application) {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _lastAdded = MutableLiveData<AudioInfo?>()
    val lastAdded: LiveData<AudioInfo?>
        get() = _lastAdded

    private var _audioPlaylist: LiveData<List<AudioInfo>>
    val audioPlaylist: LiveData<List<AudioInfo>>
        get() = _audioPlaylist

    init {
        initializeLastAdded()
        _audioPlaylist = database.getAllAudio()
    }

    private fun initializeLastAdded() {
        uiScope.launch {
            _lastAdded.value = getLastAudioFromDatabase()
        }
    }

    private suspend fun getLastAudioFromDatabase(): AudioInfo? {
        return withContext(Dispatchers.IO) {
            database.getLastAudio()
        }
    }

    private fun initializeAudioPlaylist() {
        uiScope.launch {
            _audioPlaylist = getAudioPlaylistFromDatabase()
        }
    }

    private suspend fun getAudioPlaylistFromDatabase(): LiveData<List<AudioInfo>> {
        return withContext(Dispatchers.IO) {
            database.getAllAudio()
        }
    }
}