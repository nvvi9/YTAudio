package com.example.ytaudio.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.service.MediaPlaybackServiceConnection

class MainActivityViewModel(
    private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection
) : ViewModel() {

    val rootMediaId: LiveData<String> =
        Transformations.map(mediaPlaybackServiceConnection.isConnected) {
            if (it) {
                mediaPlaybackServiceConnection.rootMediaId
            } else {
                null
            }
        }


    class Factory(private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(mediaPlaybackServiceConnection) as T
        }
    }
}