package com.nvvi9.ytaudio.service.playback

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.nvvi9.ytaudio.domain.PlaylistUseCases
import com.nvvi9.ytaudio.utils.extensions.id
import com.nvvi9.ytaudio.utils.extensions.title
import com.nvvi9.ytaudio.utils.extensions.toMediaSource


class PlaybackPreparer(
    private val playlistUseCases: PlaylistUseCases,
    private val exoPlayer: ExoPlayer,
    private val dataSourceFactory: DataSource.Factory
) : MediaSessionConnector.PlaybackPreparer {

    private var mediaSource: ConcatenatingMediaSource? = null
    private var currentMetadata = emptyList<MediaMetadataCompat>()
    private var nowPlaying: MediaMetadataCompat? = null

    private val metadataObserver = Observer<List<MediaMetadataCompat>> {
        it?.let {
            update(it)
        }
//        if (!it.metadataEquals(currentMetadata)) {
//            updateMetadata(it)
//        }
    }

    init {
        playlistUseCases.getMediaMetadata().observeForever(metadataObserver)
    }

    private fun update(new: List<MediaMetadataCompat>) {
        currentMetadata = new
        val isPlaying = exoPlayer.isPlaying
        mediaSource = new.toMediaSource(dataSourceFactory).also {
            exoPlayer.setMediaSource(it)
            exoPlayer.playWhenReady = isPlaying
        }
    }

    private fun updateMetadata(newMetadata: List<MediaMetadataCompat>) {
        currentMetadata = newMetadata
        mediaSource = currentMetadata.toMediaSource(dataSourceFactory)

        val isPlaying = exoPlayer.isPlaying
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

    fun cancel() {
        playlistUseCases.getMediaMetadata().removeObserver(metadataObserver)
    }

    override fun getSupportedPrepareActions() =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PREPARE_FROM_URI or
                PlaybackStateCompat.ACTION_PLAY_FROM_URI


    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        currentMetadata.indexOfFirst { it.id == mediaId }.takeIf { it != -1 }?.let {
            try {
                exoPlayer.run {
                    prepare()
                    seekTo(it, 0)
                    this.playWhenReady = playWhenReady
                }
            } catch (t: Throwable) {
                Log.e(javaClass.simpleName, t.stackTraceToString())
            }
        } ?: Log.w(javaClass.simpleName, "Content not found: id=$mediaId")
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