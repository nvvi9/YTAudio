package com.example.ytaudio.screens.player

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.databinding.PlayerFragmentBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.*

class PlayerFragment : Fragment(), Player.EventListener {

    private lateinit var binding: PlayerFragmentBinding
    private lateinit var player: SimpleExoPlayer
    private lateinit var playerViewModel: PlayerViewModel
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private lateinit var url: String
//    private lateinit var audio: AudioInfo
//    private var playbackPosition = 0L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.player_fragment, container, false)
        val application = requireNotNull(this.activity).application

        val dataSource = AudioDatabase.getInstance(application).audioDatabaseDao

        val viewModelFactory = PlayerViewModelFactory(dataSource, application)
        playerViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(PlayerViewModel::class.java)

        binding.lifecycleOwner = this


        playerViewModel.onShowToast.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(
                    this.requireContext(),
                    if (playerViewModel.audioPlaylist.value.isNullOrEmpty()) "null" else "something",
                    Toast.LENGTH_SHORT
                ).show()

                playerViewModel.doneShowingToast()
            }
        })

        url = playerViewModel.lastAdded.value?.audioUri ?: "nothing"
        url = "null"
//        Toast.makeText(this.requireContext(), url, Toast.LENGTH_SHORT).show()


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24)
            initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24 || !this::player.isInitialized)
            initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24)
            releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24)
            releasePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this.requireContext())
        binding.playerView.player = player
//        val mediaSource = buildMediaSource(Uri.parse(playerViewModel.lastAdded.value?.audioUri))
        val mediaSource = buildMediaSource(Uri.parse(url))
        player.playWhenReady = playWhenReady
        player.seekTo(currentWindow, playbackPosition)
        player.prepare(mediaSource)
    }

    private fun releasePlayer() {
        if (this::player.isInitialized) {
            playWhenReady = player.playWhenReady
            playbackPosition = player.currentPosition
            currentWindow = player.currentWindowIndex
            player.release()
        }
    }

    private fun hideSystemUi() {
        binding.playerView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private fun buildMediaSource(uri: Uri) = ProgressiveMediaSource.Factory(
        DefaultDataSourceFactory(
            this.requireContext(),
            "user-agent"
        )
    ).createMediaSource(uri)
}