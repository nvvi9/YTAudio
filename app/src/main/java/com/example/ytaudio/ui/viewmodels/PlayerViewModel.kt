package com.example.ytaudio.ui.viewmodels

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.ytaudio.R
import com.example.ytaudio.service.AudioServiceConnection
import com.example.ytaudio.service.EMPTY_PLAYBACK_STATE
import com.example.ytaudio.service.NOTHING_PLAYING
import com.example.ytaudio.utils.extensions.*
import com.example.ytaudio.vo.NowPlayingInfo
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlayerViewModel @Inject constructor(
    audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    private var playbackState = EMPTY_PLAYBACK_STATE
    private val _currentAudioInfo = MutableLiveData<NowPlayingInfo>()
    val currentAudioInfo: LiveData<NowPlayingInfo> get() = _currentAudioInfo

    private var updatePosition = true
    private val handler = Handler(Looper.getMainLooper())

    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        val position = playbackState.currentPlayBackPosition
        if (_currentAudioInfo.value?.currentPosition != position) {
            _currentAudioInfo.postValue(_currentAudioInfo.value?.apply {
                currentPosition = position
            })
        }
        if (updatePosition) {
            checkPlaybackPosition()
        }
    }, POSITION_UPDATE_INTERVAL_MILLIS)

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val data = audioServiceConnection.nowPlaying.value ?: NOTHING_PLAYING
        updateState(playbackState, data)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        updateState(playbackState, it)
    }

    private val mediaPlaybackServiceConnection = audioServiceConnection.apply {
        playbackState.observeForever(playbackStateObserver)
        nowPlaying.observeForever(mediaMetadataObserver)
        checkPlaybackPosition()
    }

    companion object {
        private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlaybackServiceConnection.playbackState.removeObserver(playbackStateObserver)
        mediaPlaybackServiceConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        updatePosition = false
    }

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ) {
        if (mediaMetadata.duration != 0L && mediaMetadata.id != null) {
            val nowPlayingAudioInfo =
                NowPlayingInfo(
                    mediaMetadata.id!!,
                    mediaMetadata.title,
                    mediaMetadata.displaySubtitle,
                    mediaMetadata.albumArtUri,
                    mediaMetadata.duration
                )
            _currentAudioInfo.postValue(nowPlayingAudioInfo)
        }

        _currentAudioInfo.postValue(_currentAudioInfo.value?.apply {
            audioButtonRes = if (playbackState.isPlaying) {
                R.drawable.ic_pause_black
            } else {
                R.drawable.ic_play_arrow_black
            }
        })
    }
}