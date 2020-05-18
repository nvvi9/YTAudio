package com.example.ytaudio.screens.url_link

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.database.AudioDatabaseDao
import java.lang.IllegalArgumentException

class SourceLinkViewModelFactory(
    private val dataSource: AudioDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SourceLinkViewModel::class.java))
            return SourceLinkViewModel(dataSource, application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}