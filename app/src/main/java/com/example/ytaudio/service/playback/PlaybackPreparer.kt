package com.example.ytaudio.service.playback

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.Observer
import com.example.ytaudio.domain.PlaylistUseCases
import com.example.ytaudio.utils.extensions.id
import com.example.ytaudio.utils.extensions.metadataEquals
import com.example.ytaudio.utils.extensions.title
import com.example.ytaudio.utils.extensions.toMediaSource
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.upstream.DataSource


class PlaybackPreparer(
    private val playlistUseCases: PlaylistUseCases,
    private val exoPlayer: ExoPlayer,
    private val dataSourceFactory: DataSource.Factory
) : MediaSessionConnector.PlaybackPreparer {

    private var mediaSource: ConcatenatingMediaSource? = null
    private var currentMetadata = emptyList<MediaMetadataCompat>()
    private var nowPlaying: MediaMetadataCompat? = null

    private val metadataObserver = Observer<List<MediaMetadataCompat>> {
        if (!it.metadataEquals(currentMetadata)) {
            updateMetadata(it)
        }
    }

    init {
        playlistUseCases.getMediaMetadata().observeForever(metadataObserver)
    }

    private fun updateMetadata(newMetadata: List<MediaMetadataCompat>) {
        currentMetadata = newMetadata
        mediaSource = currentMetadata.toMediaSource(dataSourceFactory)

        val isPlaying = exoPlayer.playbackState == Player.TIMELINE_CHANGE_REASON_PREPARED
        var position = exoPlayer.currentPosition
        val window = nowPlaying?.let { playing ->
            if (playing.id in currentMetadata.map { it.id }) {
                currentMetadata.indexOf(playing)
            } else {
                position = 0
                currentMetadata.indexOfFirst {
                    it.title!! > playing.title!!
                }.takeIf { it != -1 } ?: currentMetadata.indexOfLast {
                    it.title!! < playing.title!!
                }.takeIf { it != -1 } ?: 0
            }
        } ?: 0

        try {
            exoPlayer.apply {
                playWhenReady = false
                mediaSource?.let { prepare(it) }
                seekTo(window, position)
                nowPlaying = currentMetadata[position.toInt()]
                playWhenReady = isPlaying && currentMetadata.isNotEmpty()
            }
        } catch (t: Throwable) {
            Log.e(javaClass.simpleName, t.toString())
        }
    }

    fun onCancel() {
        playlistUseCases.getMediaMetadata().removeObserver(metadataObserver)
    }

    override fun getSupportedPrepareActions() =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH


    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        val itemToPlay = currentMetadata.find { it.id == mediaId }

        itemToPlay?.let { item ->
            val initialWindowIndex = currentMetadata.indexOf(item)
            mediaSource?.let { exoPlayer.prepare(it) }
            exoPlayer.seekTo(initialWindowIndex, 0)
            exoPlayer.playWhenReady = playWhenReady
            nowPlaying = item
        } ?: Log.w(javaClass.name, "Content not found: id=$mediaId")
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