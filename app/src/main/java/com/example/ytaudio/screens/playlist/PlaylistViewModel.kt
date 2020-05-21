package com.example.ytaudio.screens.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistViewModel(
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

//    private val _audioPlaylist: MutableLiveData<List<AudioInfo>>()
//    val audioPlaylist: LiveData<List<AudioInfo>>
//        get() = _audioPlaylist

    fun getAudioPlaylist() = database.getAllAudio()
}