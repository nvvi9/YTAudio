package com.nvvi9.ytaudio.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nvvi9.ytaudio.databinding.FragmentPlayerBinding
import com.nvvi9.ytaudio.di.Injectable
import com.nvvi9.ytaudio.ui.adapters.PlayerListener
import com.nvvi9.ytaudio.ui.adapters.setRaw
import com.nvvi9.ytaudio.ui.viewmodels.PlayerViewModel
import com.nvvi9.ytaudio.utils.extensions.fixPercentBounds
import com.nvvi9.ytaudio.utils.extensions.fixToPercent
import com.nvvi9.ytaudio.utils.extensions.fixToStep
import com.nvvi9.ytaudio.utils.extensions.percentToMillis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class PlayerFragment :
    Fragment(),
    PlayerListener,
    Injectable {

    @Inject
    lateinit var playerViewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentPlayerBinding

    private val playerViewModel by viewModels<PlayerViewModel> {
        playerViewModelFactory
    }

    private var isUserSeeking = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater).apply {
            progressBar.run {
                onStartTracking = {
                    isUserSeeking = true
                }
                onStopTracking = {
                    isUserSeeking = false
                    playerViewModel.seekTo(
                        (it * (playerViewModel.nowPlayingInfo.value?.durationMillis
                            ?: 0) / 100).toLong()
                    )
                }
                onProgressChanged = { pos, fromUser ->
                    if (fromUser) {
                        position = (pos * (playerViewModel.nowPlayingInfo.value?.durationMillis
                            ?: 0) / 100).toInt()
                    }
                }
            }
            listener = this@PlayerFragment
            viewModel = playerViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerViewModel.run {
            nowPlayingInfo.observe(viewLifecycleOwner) {
                binding.nowPlaying = it
                Log.i(javaClass.simpleName, it?.toString() ?: "empty")
            }

            currentButtonRes.observe(viewLifecycleOwner) {
                binding.buttonRes = it
            }

            raw.observe(viewLifecycleOwner) {
                it?.let {
                    binding.progressBar.setRaw(it)
                }
            }

            shuffleMode.observe(viewLifecycleOwner) {
                it?.let {
                    binding.shuffleState = it
                }
            }

            repeatMode.observe(viewLifecycleOwner) {
                it?.let {
                    binding.repeatState = it
                }
            }

            currentPositionMillis.observe(viewLifecycleOwner) { pos ->
                if (!isUserSeeking) {
                    binding.run {
                        position = pos?.toInt()
                        progressBar.run {
                            (nowPlayingInfo.value?.durationMillis ?: 0).let { total ->
                                progress.percentToMillis(total).fixToStep(1000).takeIf {
                                    it != pos
                                }?.let {
                                    progress = pos.fixToPercent(total).fixPercentBounds()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPlayPauseClicked() {
        playerViewModel.playPause()
    }

    override fun onSkipToNextClicked() {
        playerViewModel.skipToNext()
    }

    override fun onSkipToPreviousClicked() {
        playerViewModel.skipToPrevious()
    }

    override fun onRepeatButtonClicked() {
        playerViewModel.setRepeatMode()
    }

    override fun onShuffleButtonClicked() {
        playerViewModel.setShuffleMode()
    }

    override fun onBackButtonClicked() {
        findNavController().navigateUp()
    }
}