package com.example.ytaudio.viewmodels

import android.app.Application
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.DateUtils
import androidx.lifecycle.*
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.service.EMPTY_PLAYBACK_STATE
import com.example.ytaudio.service.MediaPlaybackServiceConnection
import com.example.ytaudio.service.NOTHING_PLAYING
import com.example.ytaudio.service.extensions.*

class AudioPlayerViewModel(
    mediaPlaybackServiceConnection: MediaPlaybackServiceConnection,
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

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
        val data = mediaPlaybackServiceConnection.nowPlaying.value ?: NOTHING_PLAYING
        updateState(playbackState, data)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        updateState(playbackState, it)
    }

    private val mediaPlaybackServiceConnection = mediaPlaybackServiceConnection.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
        checkPlaybackPosition()
    }

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ) {
        if (mediaMetadata.duration != 0L && mediaMetadata.id != null) {
            val nowPlayingAudioInfo = NowPlayingAudioInfo(
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

    class Factory(
        private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection,
        private val dataSource: AudioDatabaseDao,
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AudioPlayerViewModel(
                mediaPlaybackServiceConnection,
                dataSource,
                application
            ) as T
        }
    }
}

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L