package com.example.ytaudio.ui.viewmodels

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.ytaudio.R
import com.example.ytaudio.service.AudioServiceConnection
import com.example.ytaudio.service.EMPTY_PLAYBACK_STATE
import com.example.ytaudio.service.NOTHING_PLAYING
import com.example.ytaudio.utils.extensions.*
import javax.inject.Inject


class PlayerViewModel @Inject constructor(
    audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    data class NowPlayingAudioInfo(
        val audioId: String,
        val thumbnailUri: Uri,
        val title: String?,
        val subtitle: String?,
        val duration: String
    )

    private var playbackState = EMPTY_PLAYBACK_STATE
    val currentAudioInfo = MutableLiveData<NowPlayingAudioInfo>()
    val audioPosition = MutableLiveData<Long>().apply { postValue(0L) }
    val audioButtonRes = MutableLiveData<Int>().apply { postValue(R.drawable.ic_album_black) }

    private var updatePosition = true
    private val handler = Handler(Looper.getMainLooper())

    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        val currentPosition = playbackState.currentPlayBackPosition
        if (audioPosition.value != currentPosition)
            audioPosition.postValue(currentPosition)
        if (updatePosition)
            checkPlaybackPosition()
    }, POSITION_UPDATE_INTERVAL_MILLIS)

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val data = audioServiceConnection.nowPlaying.value ?: NOTHING_PLAYING
        updateState(playbackState, data)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        updateState(playbackState, it)
    }

    private val mediaPlaybackServiceConnection = audioServiceConnection.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
        checkPlaybackPosition()
    }

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ) {
        if (mediaMetadata.duration != 0L && mediaMetadata.id != null) {
            val nowPlayingAudioInfo =
                NowPlayingAudioInfo(
                    mediaMetadata.id!!,
                    mediaMetadata.albumArtUri,
                    mediaMetadata.title,
                    mediaMetadata.displaySubtitle,
                    DateUtils.formatElapsedTime(mediaMetadata.duration)
                )
            currentAudioInfo.postValue(nowPlayingAudioInfo)
        }

        audioButtonRes.postValue(
            if (playbackState.isPlaying) R.drawable.ic_pause_black
            else R.drawable.ic_play_arrow_black
        )
    }

    override fun onCleared() {
        super.onCleared()

        mediaPlaybackServiceConnection.playbackState.removeObserver(playbackStateObserver)
        mediaPlaybackServiceConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        updatePosition = false
    }
}

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L