package com.example.ytaudio.service

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.Observer
import com.example.ytaudio.service.extensions.id
import com.example.ytaudio.service.extensions.title
import com.example.ytaudio.service.extensions.toMediaSource
import com.example.ytaudio.service.library.DatabaseAudioSource
import com.example.ytaudio.utils.Event
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.upstream.DataSource


class PlaybackPreparer(
    private val audioSource: DatabaseAudioSource,
    private val exoPlayer: ExoPlayer,
    private val dataSourceFactory: DataSource.Factory
) : MediaSessionConnector.PlaybackPreparer {

    private var mediaSource: ConcatenatingMediaSource? = null
    private var metadataArray = emptyArray<MediaMetadataCompat>()

    private val itemsUpdatedObserver = Observer<Event<List<MediaMetadataCompat>>> { event ->
        event.getContentIfNotHandled()?.sortedBy { it.title }?.let { list ->
            metadataArray = list.toTypedArray()
            mediaSource = list.toMediaSource(dataSourceFactory)

            if (mediaSource != null && exoPlayer.isPlaying) {
                val currentPosition = exoPlayer.currentPosition
                val currentWindow = exoPlayer.currentWindowIndex

                exoPlayer.apply {
                    exoPlayer.playWhenReady = false
                    prepare(mediaSource!!)
                    seekTo(currentWindow, currentPosition)
                    playWhenReady = true
                }
            }
        }
    }

    init {
        audioSource.itemsUpdatedEvent.observeForever(itemsUpdatedObserver)
    }

    fun removeObservers() {
        audioSource.itemsUpdatedEvent.removeObserver(itemsUpdatedObserver)
    }

    override fun getSupportedPrepareActions() =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH


    override fun onPrepareFromMediaId(
        mediaId: String,
        playWhenReady: Boolean,
        extras: Bundle?
    ) {
        audioSource.whenReady {
            val itemToPlay = metadataArray.find { item ->
                item.id == mediaId
            }

            itemToPlay?.let { item ->
                val initialWindowIndex = metadataArray.indexOf(item)
                mediaSource?.let { exoPlayer.prepare(it) }
                exoPlayer.seekTo(initialWindowIndex, 0)
                exoPlayer.playWhenReady = playWhenReady
            } ?: Log.w(javaClass.name, "Content not found: id=$mediaId")
        }

        Log.e(javaClass.simpleName, "NOT READY $mediaId")
    }

    override fun onCommand(
        player: Player,
        controlDispatcher: ControlDispatcher,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ) = false

    override fun onPrepare(playWhenReady: Boolean) = Unit
    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit
    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit
}