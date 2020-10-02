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
import com.google.android.exoplayer2.IllegalSeekPositionException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DataSource
import com.nvvi9.ytaudio.domain.AudioInfoUseCases
import com.nvvi9.ytaudio.utils.extensions.id
import com.nvvi9.ytaudio.utils.extensions.toMediaSource


class PlaybackPreparer(
    private val audioInfoUseCases: AudioInfoUseCases,
    private val exoPlayer: ExoPlayer,
    private val dataSourceFactory: DataSource.Factory
) : MediaSessionConnector.PlaybackPreparer {

    private var currentMetadata = emptyList<MediaMetadataCompat>()

    private val metadataObserver = Observer<List<MediaMetadataCompat>> {
        it?.let { update(it) }
    }

    init {
        audioInfoUseCases.getMediaMetadata().observeForever(metadataObserver)
    }

    override fun getSupportedPrepareActions() =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
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

    fun cancel() {
        audioInfoUseCases.getMediaMetadata().removeObserver(metadataObserver)
    }

    private fun update(new: List<MediaMetadataCompat>) {
        val nowPlayingMediaId =
            currentMetadata.takeIf { it.isNotEmpty() }?.get(exoPlayer.currentWindowIndex)?.id
        var position = exoPlayer.currentPosition
        val window = new.map { it.id }.indexOf(nowPlayingMediaId).takeIf { it != -1 }
            ?: exoPlayer.currentWindowIndex.also { position = 0 }
        currentMetadata = new
        val isPlaying = exoPlayer.isPlaying
        exoPlayer.setMediaSource(new.toMediaSource(dataSourceFactory))
        if (currentMetadata.isNotEmpty()) {
            try {
                exoPlayer.prepare()
                exoPlayer.seekTo(window, position)
            } catch (e: IllegalSeekPositionException) {
                exoPlayer.seekToDefaultPosition(currentMetadata.size - 1)
            } finally {
                exoPlayer.playWhenReady = isPlaying
            }
        }
    }
}