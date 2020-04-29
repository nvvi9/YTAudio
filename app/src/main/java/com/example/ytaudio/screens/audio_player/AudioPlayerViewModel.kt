package com.example.ytaudio.screens.audio_player

import android.media.MediaPlayer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.ytaudio.R
import kotlin.coroutines.coroutineContext

class AudioPlayerViewModel : ViewModel() {

    private val _currentTime = MutableLiveData<Int>()
    val currentTime: LiveData<Int>
        get() = _currentTime

    val currentTimeString = Transformations.map(currentTime) {
        DateUtils.formatElapsedTime(it.toLong())
    }

    private val _mediaPlayer = MutableLiveData<MediaPlayer>()
    val mediaPlayer: LiveData<MediaPlayer>
        get() = _mediaPlayer


    init {
        _currentTime.value = 0
    }
}