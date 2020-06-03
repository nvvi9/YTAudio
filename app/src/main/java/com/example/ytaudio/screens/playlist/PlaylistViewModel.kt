package com.example.ytaudio.screens.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.ytaudio.database.AudioDatabaseDao


class PlaylistViewModel(
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

//    private val viewModelJob = Job()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val audioPlaylist = database.getAllAudio()
}