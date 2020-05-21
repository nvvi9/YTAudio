package com.example.ytaudio.screens.audio_player

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.database.AudioDatabaseDao

class AudioPlayerViewModelFactory(
    private val dataSource: AudioDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioPlayerViewModel::class.java))
            return AudioPlayerViewModel(dataSource, application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}