package com.nvvi9.ytaudio.service

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nvvi9.ytaudio.utils.extensions.id
import javax.inject.Singleton


@Singleton
class AudioServiceConnection(private val context: Context, serviceComponent: ComponentName) :
    MediaBrowserCompat.ConnectionCallback() {

    private val _isConnected = MutableLiveData<Boolean>().apply { postValue(false) }
    val isConnected: LiveData<Boolean> get() = _isConnected

    private val _networkFailure = MutableLiveData<Boolean>().apply { postValue(false) }
    val networkFailure: LiveData<Boolean> get() = _networkFailure

    private val _playbackState =
        MutableLiveData<PlaybackStateCompat>().apply { postValue(EMPTY_PLAYBACK_STATE) }
    val playbackState: LiveData<PlaybackStateCompat> get() = _playbackState

    private val _nowPlaying =
        MutableLiveData<MediaMetadataCompat>().apply { postValue(NOTHING_PLAYING) }
    val nowPlaying: LiveData<MediaMetadataCompat> get() = _nowPlaying

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val _repeatMode = MutableLiveData<Int>().apply { postValue(REPEAT_MODE_NONE) }
    val repeatMode: LiveData<Int> get() = _repeatMode

    private val _shuffleMode = MutableLiveData<Int>().apply { postValue(SHUFFLE_MODE_NONE) }
    val shuffleMode: LiveData<Int> get() = _shuffleMode

    val rootMediaId get() = mediaBrowser.root

    private lateinit var mediaController: MediaControllerCompat

    private val mediaBrowser =
        MediaBrowserCompat(context, serviceComponent, this, null).apply {
            connect()
        }

    override fun onConnected() {
        mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
            registerCallback(mediaControllerCallback)
        }
        _isConnected.postValue(true)
    }

    override fun onConnectionSuspended() {
        _isConnected.postValue(false)
    }

    override fun onConnectionFailed() {
        _isConnected.postValue(false)
    }

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    fun sendCommand(command: String, parameters: Bundle? = null) =
        sendCommand(command, parameters) { _, _ -> }

    private fun sendCommand(
        command: String,
        parameters: Bundle?,
        resultCallback: ((Int, Bundle?) -> Unit)
    ) = if (mediaBrowser.isConnected) {
        mediaController.sendCommand(command, parameters, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                resultCallback(resultCode, resultData)
            }
        })
        true
    } else {
        false
    }

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _nowPlaying.postValue(if (metadata?.id == null) NOTHING_PLAYING else metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                NETWORK_FAILURE -> _networkFailure.postValue(true)
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _repeatMode.postValue(repeatMode)
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            _shuffleMode.postValue(shuffleMode)
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {}

        override fun onSessionDestroyed() {
            onConnectionSuspended()
        }
    }

    companion object {
        @Volatile
        private var instance: AudioServiceConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName) =
            instance ?: synchronized(this) {
                instance ?: AudioServiceConnection(context, serviceComponent)
                    .also { instance = it }
            }
    }
}

val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()