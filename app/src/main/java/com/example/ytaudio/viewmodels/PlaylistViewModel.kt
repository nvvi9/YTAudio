package com.example.ytaudio.viewmodels

import android.app.Application
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.lifecycle.*
import com.example.ytaudio.AudioItem
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.service.EMPTY_PLAYBACK_STATE
import com.example.ytaudio.service.MediaPlaybackServiceConnection
import com.example.ytaudio.service.NOTHING_PLAYING
import com.example.ytaudio.service.extensions.id
import com.example.ytaudio.service.extensions.isPlaying
import com.example.ytaudio.utils.needUpdate
import com.example.ytaudio.utils.updateInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData
import kotlinx.coroutines.*


class PlaylistViewModel(
    private val audioId: String,
    mediaPlaybackServiceConnection: MediaPlaybackServiceConnection,
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val audioInfoList = Transformations.map(database.getAllAudio()) {
        it?.forEach { audio ->
            if (audio.needUpdate) {
                updateAudio(audio)
            }
        }
        it
    }.observeForever {}

    private fun updateAudio(audio: AudioInfo) {
        uiScope.launch {
            audio.updateInfo()
            database.update(audio)
        }
    }

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

    private val _audioItemList = MutableLiveData<List<AudioItem>>()
    val audioItemList: LiveData<List<AudioItem>> = _audioItemList

    val networkFailure = Transformations.map(mediaPlaybackServiceConnection.networkFailure) { it }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: List<MediaBrowserCompat.MediaItem>
        ) {
            val items = children.map {
                AudioItem(
                    it.mediaId!!,
                    it.description.title.toString(),
                    it.description.subtitle?.toString() ?: "",
                    it.description.iconUri!!,
                    getPlaybackStatus(it.mediaId!!)
                )
            }
            _audioItemList.postValue(items)
        }
    }

    private val mediaPlaybackServiceConnection = mediaPlaybackServiceConnection.also {
        it.subscribe(audioId, subscriptionCallback)
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
    }

    private fun getPlaybackStatus(audioId: String): Int {
        val isActive = audioId == mediaPlaybackServiceConnection.nowPlaying.value?.id
        val isPlaying = mediaPlaybackServiceConnection.playbackState.value?.isPlaying ?: false

        return when {
            !isActive -> 0
            isPlaying -> R.drawable.ic_pause_black
            else -> R.drawable.ic_play_arrow_black
        }
    }

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ): List<AudioItem> {
        val newPlaybackStatus = if (playbackState.isPlaying) R.drawable.ic_pause_black
        else R.drawable.ic_play_arrow_black

        return audioItemList.value?.map {
            val playbackStatus = if (it.audioId == mediaMetadata.id) newPlaybackStatus else 0
            it.copy(playbackStatus = playbackStatus)
        } ?: emptyList()
    }

    private val extractor = object : YoutubeJExtractor() {
        suspend fun getVideoData(videoId: String?): YoutubeVideoData {
            return withContext(Dispatchers.IO) {
                super.extract(videoId)
            }
        }
    }


    fun onExtract(youtubeUrl: String) {
        val youtubeId = youtubeUrl.takeLastWhile { it != '=' && it != '/' }

        uiScope.launch {
            try {
                val videoData = extractor.getVideoData(youtubeId)

                if (videoData.videoDetails.isLiveContent) {
                    showToast("live content")
                    return@launch
                }

                val adaptiveAudioStream = videoData.streamingData.adaptiveAudioStreams.maxBy {
                    it.averageBitrate
                }

                database.insert(videoData.run {
                    AudioInfo(
                        youtubeId = videoDetails.videoId,
                        audioUrl = adaptiveAudioStream!!.url,
                        photoUrl = videoDetails.thumbnail.thumbnails.maxBy { it.height }!!.url,
                        audioTitle = videoDetails.title,
                        author = videoDetails.author,
                        authorId = videoDetails.channelId,
                        description = videoDetails.shortDescription,
                        keywords = videoDetails.keywords.joinToString(),
                        viewCount = videoDetails.viewCount.toIntOrNull() ?: 0,
                        averageRating = videoDetails.averageRating,
                        audioFormat = adaptiveAudioStream.extension,
                        codec = adaptiveAudioStream.codec,
                        bitrate = adaptiveAudioStream.bitrate,
                        averageBitrate = adaptiveAudioStream.averageBitrate,
                        audioDurationSeconds = videoDetails.lengthSeconds.toLong(),
                        lastUpdateTimeSeconds = System.currentTimeMillis() / 1000,
                        urlActiveTimeSeconds = streamingData.expiresInSeconds.toLong()
                    )
                })
            } catch (e: ExtractionException) {
                showToast("Extraction failed")
            } catch (e: YoutubeRequestException) {
                showToast("Check your connection")
            } catch (e: Exception) {
                showToast("Unknown error")
            }
        }
    }


    private fun showToast(message: String) =
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()

    override fun onCleared() {
        super.onCleared()

        mediaPlaybackServiceConnection.playbackState.removeObserver(playbackStateObserver)
        mediaPlaybackServiceConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        mediaPlaybackServiceConnection.unsubscribe(audioId, subscriptionCallback)

        viewModelJob.cancel()
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