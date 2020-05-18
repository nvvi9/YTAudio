package com.example.ytaudio.screens.player

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.database.AudioDatabaseDao

class PlayerViewModelFactory(
    private val dataSource: AudioDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java))
            return PlayerViewModel(dataSource, application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}