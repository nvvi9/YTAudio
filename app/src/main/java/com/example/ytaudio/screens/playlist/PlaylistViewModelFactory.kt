package com.example.ytaudio.screens.playlist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.database.AudioDatabaseDao

class PlaylistViewModelFactory(
    private val dataSource: AudioDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistViewModel::class.java))
            return PlaylistViewModel(dataSource, application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}