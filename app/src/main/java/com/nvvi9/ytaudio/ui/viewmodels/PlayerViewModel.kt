package com.nvvi9.ytaudio.ui.viewmodels

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import androidx.lifecycle.*
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.service.EMPTY_PLAYBACK_STATE
import com.nvvi9.ytaudio.service.NOTHING_PLAYING
import com.nvvi9.ytaudio.utils.extensions.*
import com.nvvi9.ytaudio.vo.NowPlayingInfo
import com.nvvi9.ytaudio.vo.PlaybackState
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


class PlayerViewModel @Inject constructor(
    private val audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    private var playbackStateCompat = EMPTY_PLAYBACK_STATE

    private val _nowPlayingInfo = MutableLiveData<NowPlayingInfo>()
    val nowPlayingInfo: LiveData<NowPlayingInfo> get() = _nowPlayingInfo

    private val _currentPositionMillis = MutableLiveData<Long>()
    val currentPositionMillis: LiveData<Long> get() = _currentPositionMillis

    private val _currentButtonRes = MutableLiveData<Int>()
    val currentButtonRes: LiveData<Int> get() = _currentButtonRes

    private val _raw = MutableLiveData<ByteArray>().apply { postValue(byteArrayOf()) }
    val raw: LiveData<ByteArray> get() = _raw

    val playbackState: LiveData<PlaybackState>
        get() = Transformations.map(audioServiceConnection.playbackState) {
            PlaybackState.fromPlaybackStateCompat(it)
        }

    init {
        Log.i("PlayerViewModel", "initialized")
    }

    private var updatePosition = true
    private val handler = Handler(Looper.getMainLooper())

    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        playbackStateCompat.currentPlayBackPosition.takeIf {
            it != currentPositionMillis.value
        }?.let {
            updateTimePosition(it)
        }
        if (updatePosition) {
            checkPlaybackPosition()
        }
    }, POSITION_UPDATE_INTERVAL_MILLIS)

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackStateCompat = it ?: EMPTY_PLAYBACK_STATE
        updateState(playbackStateCompat, audioServiceConnection.nowPlaying.value ?: NOTHING_PLAYING)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        updateState(playbackStateCompat, it)
    }

    init {
        audioServiceConnection.run {
            playbackState.observeForever(playbackStateObserver)
            nowPlaying.observeForever(mediaMetadataObserver)
            checkPlaybackPosition()
        }
    }

    companion object {
        private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
    }

    override fun onCleared() {
        super.onCleared()
        audioServiceConnection.playbackState.removeObserver(playbackStateObserver)
        audioServiceConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        updatePosition = false
    }

    fun updateTimePosition(new: Long) {
        _currentPositionMillis.postValue(new)
    }

    fun playPause(audioId: String) {
        audioServiceConnection.takeIf { it.nowPlaying.value?.id == audioId }?.run {
            playbackState.value?.let {
                when {
                    it.isPlaying -> transportControls.pause()
                    else -> transportControls.play()
                }
            }
        }
    }

    fun seekTo(positionMillis: Long) {
        audioServiceConnection.transportControls.seekTo(positionMillis)
    }

    fun skipToNext() {
        audioServiceConnection.transportControls.skipToNext()
    }

    fun skipToPrevious() {
        audioServiceConnection.transportControls.skipToPrevious()
    }

    fun setRepeatMode() {
        audioServiceConnection.transportControls.setRepeatMode(
            when (playbackState.value?.repeatMode ?: PlaybackState()) {
                REPEAT_MODE_ONE -> REPEAT_MODE_ALL
                REPEAT_MODE_ALL -> REPEAT_MODE_NONE
                else -> REPEAT_MODE_ONE
            }
        )
    }

    fun setShuffleMode() {
        audioServiceConnection.transportControls.setShuffleMode(
            when (playbackState.value?.repeatMode ?: PlaybackState()) {
                SHUFFLE_MODE_ALL -> SHUFFLE_MODE_NONE
                else -> SHUFFLE_MODE_ALL
            }
        )
    }

    private fun updateState(
        playbackStateCompat: PlaybackStateCompat,
        metadata: MediaMetadataCompat
    ) {
        metadata.takeIf { it.duration != 0L && it.id != null }?.let {
            _nowPlayingInfo.postValue(
                NowPlayingInfo(
                    it.id, it.title, it.displaySubtitle, it.albumArtUri, it.duration * 1000
                )
            )
            viewModelScope.launch {
                _raw.postValue(Random.nextBytes(it.duration.toInt() * 1000))
            }
        }

        _currentButtonRes.postValue(
            if (playbackStateCompat.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        )
    }
}