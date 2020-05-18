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

    init {
        initializeLast()
    }

    private fun initializeLast() {
        uiScope.launch {
            _lastAdded.value = getLastAddedFromDatabase()
        }
    }

    private suspend fun getLastAddedFromDatabase(): AudioInfo? {
        return withContext(Dispatchers.IO) {
            database.getLastAudio()
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}