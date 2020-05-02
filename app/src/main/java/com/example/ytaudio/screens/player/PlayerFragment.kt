package com.example.ytaudio.screens.player

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.ytaudio.R
import com.example.ytaudio.databinding.PlayerFragmentBinding
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.audio_player_fragment.*
import kotlinx.android.synthetic.main.player_fragment.view.*

class PlayerFragment : Fragment() {

    private lateinit var binding: PlayerFragmentBinding
    private lateinit var player: SimpleExoPlayer
    private lateinit var audioUri: Uri
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.player_fragment, container, false)

        val playerFragmentArgs by navArgs<PlayerFragmentArgs>()

        audioUri = Uri.parse(playerFragmentArgs.audioUrl)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24 || !this::player.isInitialized) initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) releasePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this.requireContext())
        binding.playerView.player = player
        val mediaSource = buildMediaSource(audioUri)
        player.apply {
            
            playWhenReady = playWhenReady
            seekTo(currentWindow, playbackPosition)
            prepare(mediaSource, false, false)
        }
    }

    private fun buildMediaSource(uri: Uri) =
        ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this.context, "exoplayer"))
            .createMediaSource(uri)

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.playerView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private fun releasePlayer() {
        if (this::player.isInitialized) {
            playWhenReady = player.playWhenReady
            playbackPosition = player.currentPosition
            currentWindow = player.currentWindowIndex
            player.release()
        }
    }
}