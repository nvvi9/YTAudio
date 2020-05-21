package com.example.ytaudio.screens.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import kotlinx.coroutines.*

class PlayerViewModel(
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _lastAdded = MutableLiveData<AudioInfo?>()
    val lastAdded: LiveData<AudioInfo?>
        get() = _lastAdded

    private val _audioPlaylist = MutableLiveData<List<AudioInfo>>()
    val audioPlaylist: LiveData<List<AudioInfo>>
        get() = _audioPlaylist

    private val _onShowToast = MutableLiveData<Boolean>()
    val onShowToast: LiveData<Boolean>
        get() = _onShowToast

    init {
        _onShowToast.value = false
        initializeLast()
    }

    private fun initializeLast() {
        uiScope.launch {
            _lastAdded.value = getLastAddedFromDatabase()
        }
    }

    private suspend fun getLastAddedFromDatabase(): AudioInfo? {
        return withContext(Dispatchers.IO) {
            val audioInfo = database.getLastAudio()
            audioInfo
        }
    }

//    private fun initializePlaylist() {
//        uiScope.launch {
//            _audioPlaylist.value = getPlaylistFromDatabase()
//        }
//        _onShowToast.value = true
//    }
//
//    private suspend fun getPlaylistFromDatabase(): List<AudioInfo> {
//        return withContext(Dispatchers.IO) {
//            database.getAllAudio()
//        }
//    }

    fun doneShowingToast() {
        _onShowToast.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}