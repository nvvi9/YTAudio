package com.example.ytaudio.screens.audio_player

import android.app.Application
import androidx.lifecycle.*
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    val database: AudioDatabaseDao,
    application: Application
) :
    AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isAudioInfoInitialized = MutableLiveData(false)
    val isAudioInfoInitialized: LiveData<Boolean>
        get() = _isAudioInfoInitialized


    var audioInfo: AudioInfo? = null


    fun getAudioInfoFromDatabase(id: Long) {
        uiScope.launch {
            audioInfo = database.get(id)
            _isAudioInfoInitialized.value = true
        }
    }

    fun initializationDone() {
        _isAudioInfoInitialized.value = false
    }

    class Factory(
        private val dataSource: AudioDatabaseDao,
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AudioPlayerViewModel(dataSource, application) as T
        }
    }
}