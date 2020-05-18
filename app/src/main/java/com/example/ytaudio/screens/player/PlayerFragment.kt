package com.example.ytaudio.screens.player

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.databinding.PlayerFragmentBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

class PlayerFragment : Fragment(), Player.EventListener {
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
//        super.onPlaybackParametersChanged(playbackParameters)
    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {
//        super.onTracksChanged(trackGroups, trackSelections)
    }

    override fun onPlayerError(error: ExoPlaybackException) {
//        super.onPlayerError(error)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            binding.progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY)
            binding.progressBar.visibility = View.INVISIBLE
    }

    override fun onLoadingChanged(isLoading: Boolean) {
//        super.onLoadingChanged(isLoading)
    }

    override fun onPositionDiscontinuity(reason: Int) {
//        super.onPositionDiscontinuity(reason)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
//        super.onRepeatModeChanged(repeatMode)
    }

    override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {
//        super.onTimelineChanged(timeline, manifest, reason)
    }

    private lateinit var binding: PlayerFragmentBinding
    private lateinit var player: ExoPlayer
    private lateinit var url: String
    private lateinit var audio: AudioInfo
    private var playbackPosition = 0L

    private val bandwidthMeter by lazy {
        DefaultBandwidthMeter()
    }
    private val adaptiveTrackSelectionFactory by lazy {
        AdaptiveTrackSelection.Factory(bandwidthMeter)
    }
//    private var playWhenReady = true
//    private var currentWindow = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        binding = DataBindingUtil.inflate(inflater, R.layout.player_fragment, container, false)

        val dataSource = AudioDatabase.getInstance(application).audioDatabaseDao
        val viewModelFactory = PlayerViewModelFactory(dataSource, application)
        val playerViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(PlayerViewModel::class.java)

        binding.lifecycleOwner = this

        playerViewModel.lastAdded.observe(viewLifecycleOwner, Observer {
            url = it?.audioUri.toString()
            initializeExoPlayer()
        })

        return binding.root
    }

    override fun onStart() {
//        initializeExoPlayer()
        super.onStart()
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }

    private fun initializeExoPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
            this.requireContext(),
            DefaultTrackSelector(adaptiveTrackSelectionFactory),
            DefaultLoadControl()
        )

        preparePlayer()
        binding.simpleExoPlayerView.player = player
        player.seekTo(playbackPosition)
        player.playWhenReady = true
        player.addListener(this)
    }

    private fun preparePlayer() {
        player.prepare(buildMediaSource(Uri.parse(url)))
    }

    private fun releasePlayer() {
        playbackPosition = player.currentPosition
        player.release()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultHttpDataSourceFactory("ua", bandwidthMeter)
        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(dataSourceFactory)
        return DashMediaSource(uri, dataSourceFactory, dashChunkSourceFactory, null, null)
    }
}