package com.example.ytaudio.playlist

import android.app.Application
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.domain.PlaylistItem
import com.example.ytaudio.repository.AudioRepository
import com.example.ytaudio.service.EMPTY_PLAYBACK_STATE
import com.example.ytaudio.service.MediaPlaybackServiceConnection
import com.example.ytaudio.service.NOTHING_PLAYING
import com.example.ytaudio.service.extensions.id
import com.example.ytaudio.service.extensions.isPlaying


class PlaylistViewModel(
    private val audioId: String,
    mediaPlaybackServiceConnection: MediaPlaybackServiceConnection,
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val audioRepository = AudioRepository(database)

    val databaseAudioInfo = database.getAllAudio()

    val playlistItems = audioRepository.playlistItems

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        val playbackState = it ?: EMPTY_PLAYBACK_STATE
        val data = mediaPlaybackServiceConnection.nowPlaying.value ?: NOTHING_PLAYING

        data.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)?.let {
            _audioItemList.postValue(updateState(playbackState, data))
        }
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        val playbackState =
            mediaPlaybackServiceConnection.playbackState.value ?: EMPTY_PLAYBACK_STATE
        val data = it ?: NOTHING_PLAYING

        data.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)?.let {
            _audioItemList.postValue(updateState(playbackState, data))
        }
    }

    private val _audioItemList = MutableLiveData<List<PlaylistItem>>()
    val audioItemList: LiveData<List<PlaylistItem>> = _audioItemList

    val networkFailure = Transformations.map(mediaPlaybackServiceConnection.networkFailure) { it }

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

    private val mediaPlaybackServiceConnection = mediaPlaybackServiceConnection.apply {
        subscribe(audioId, subscriptionCallback)
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
        mediaPlaybackServiceConnection.unsubscribe(audioId, subscriptionCallback)
    }


    class Factory(
        private val audioId: String,
        private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection,
        private val dataSource: AudioDatabaseDao,
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PlaylistViewModel(
                audioId,
                mediaPlaybackServiceConnection,
                dataSource,
                application
            ) as T
        }
    }
}

