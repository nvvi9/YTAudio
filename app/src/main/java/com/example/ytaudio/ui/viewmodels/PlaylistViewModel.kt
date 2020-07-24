package com.example.ytaudio.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.example.ytaudio.R
import com.example.ytaudio.domain.PlaylistUseCases
import com.example.ytaudio.service.AudioServiceConnection
import com.example.ytaudio.service.EMPTY_PLAYBACK_STATE
import com.example.ytaudio.service.NOTHING_PLAYING
import com.example.ytaudio.utils.Constants.MEDIA_ROOT_ID
import com.example.ytaudio.utils.extensions.id
import com.example.ytaudio.utils.extensions.isPlaying
import com.example.ytaudio.vo.PlaylistItem
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlaylistViewModel @Inject constructor(
    private val playlistUseCases: PlaylistUseCases,
    audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    val playlistItems = playlistUseCases.playlistItems

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        val playbackState = it ?: EMPTY_PLAYBACK_STATE
        val data = audioServiceConnection.nowPlaying.value ?: NOTHING_PLAYING

        data.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)?.let {
            _audioItemList.postValue(updateState(playbackState, data))
        }
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        val playbackState =
            audioServiceConnection.playbackState.value ?: EMPTY_PLAYBACK_STATE
        val data = it ?: NOTHING_PLAYING

        data.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)?.let {
            _audioItemList.postValue(updateState(playbackState, data))
        }
    }

    private val _audioItemList = MutableLiveData<List<PlaylistItem>>()
    val audioItemList: LiveData<List<PlaylistItem>> = _audioItemList

    fun deleteFromDatabase(items: List<PlaylistItem>) {
        viewModelScope.launch {
            playlistUseCases.deleteItems(items)
        }
    }

    val networkFailure = Transformations.map(audioServiceConnection.networkFailure) { it }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: List<MediaBrowserCompat.MediaItem>
        ) {
            val items = children.map {
                PlaylistItem(
                    it.mediaId!!,
                    it.description.title.toString(),
                    it.description.subtitle.toString(),
                    it.description.iconUri.toString(),
                    0,
                    getPlaybackStatus(it.mediaId!!)
                )
            }
            _audioItemList.postValue(items)
        }
    }

    private val mediaPlaybackServiceConnection = audioServiceConnection.apply {
        subscribe(MEDIA_ROOT_ID, subscriptionCallback)
        playbackState.observeForever(playbackStateObserver)
        nowPlaying.observeForever(mediaMetadataObserver)
    }

    private fun getPlaybackStatus(audioId: String): Int {
        val isActive = audioId == mediaPlaybackServiceConnection.nowPlaying.value?.id
        val isPlaying = mediaPlaybackServiceConnection.playbackState.value?.isPlaying ?: false

        return when {
            !isActive -> 0
            isPlaying -> R.drawable.ic_play_arrow_black
            else -> R.drawable.ic_pause_black
        }
    }

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ): List<PlaylistItem> {
        val newPlaybackStatus = if (playbackState.isPlaying) R.drawable.ic_play_arrow_black
        else R.drawable.ic_pause_black

        return audioItemList.value?.map {
            val playbackStatus = if (it.id == mediaMetadata.id) newPlaybackStatus else 0
            it.copy(playbackState = playbackStatus)
        } ?: emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlaybackServiceConnection.playbackState.removeObserver(playbackStateObserver)
        mediaPlaybackServiceConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        mediaPlaybackServiceConnection.unsubscribe(MEDIA_ROOT_ID, subscriptionCallback)
    }
}
