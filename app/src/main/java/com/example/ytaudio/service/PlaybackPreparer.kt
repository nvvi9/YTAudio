package com.example.ytaudio.service

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.ytaudio.service.extensions.id
import com.example.ytaudio.service.extensions.toMediaSource
import com.example.ytaudio.service.library.AudioSource
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DataSource


class PlaybackPreparer(
    private val audioSource: AudioSource,
    private val exoPlayer: ExoPlayer,
    private val dataSourceFactory: DataSource.Factory
) : MediaSessionConnector.PlaybackPreparer {

    override fun getSupportedPrepareActions() =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH


    override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
        audioSource.whenReady {
            val itemToPlay = audioSource.find { item ->
                item.id == mediaId
            }

            itemToPlay?.let { item ->
                val metadataList = audioSource.sortedBy { it.id }
                val mediaSource = metadataList.toMediaSource(dataSourceFactory)
                val initialWindowIndex = metadataList.indexOf(item)

                exoPlayer.prepare(mediaSource)
                exoPlayer.seekTo(initialWindowIndex, 0)
            } ?: Log.w(javaClass.name, "Content not found: id=$mediaId")
        }
    }

    override fun onPrepare() = Unit
    override fun onPrepareFromSearch(query: String?, extras: Bundle?) = Unit
    override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) = Unit

    override fun onCommand(
        player: Player,
        controlDispatcher: ControlDispatcher,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ) = false
}