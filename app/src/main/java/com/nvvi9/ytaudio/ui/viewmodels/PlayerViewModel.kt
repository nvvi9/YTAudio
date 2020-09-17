package com.nvvi9.ytaudio.ui.viewmodels

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.service.EMPTY_PLAYBACK_STATE
import com.nvvi9.ytaudio.service.NOTHING_PLAYING
import com.nvvi9.ytaudio.utils.extensions.*
import com.nvvi9.ytaudio.vo.NowPlayingInfo
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlayerViewModel @Inject constructor(
    audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    private var playbackState = EMPTY_PLAYBACK_STATE
    private val _currentAudioInfo = MutableLiveData<NowPlayingInfo>()
    val currentAudioInfo: LiveData<NowPlayingInfo> get() = _currentAudioInfo.distinctUntilChanged()

    private val _currentPosition = MutableLiveData<Long>()
    val currentPosition: LiveData<Long> get() = _currentPosition

    private val _currentButtonRes = MutableLiveData<Int>()
    val currentButtonRes: LiveData<Int> get() = _currentButtonRes

    private var updatePosition = true
    private val handler = Handler(Looper.getMainLooper())

    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        playbackState.currentPlayBackPosition.takeIf {
            it != currentPosition.value
        }?.let {
            _currentPosition.postValue(it)
        }
        if (updatePosition) {
            checkPlaybackPosition()
        }
    }, POSITION_UPDATE_INTERVAL_MILLIS)

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        updateState(playbackState, audioServiceConnection.nowPlaying.value ?: NOTHING_PLAYING)
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
        mediaMetadata.takeIf {
            it.duration != 0L && it.id != null
        }?.let {
            _currentAudioInfo.postValue(
                NowPlayingInfo(it.id, it.title, it.displaySubtitle, it.albumArtUri, it.duration)
            )
        }

        _currentButtonRes.postValue(
            if (playbackState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        )
    }
}