package com.nvvi9.ytaudio.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.nvvi9.ytaudio.databinding.FragmentPlayerBinding
import com.nvvi9.ytaudio.di.Injectable
import com.nvvi9.ytaudio.ui.adapters.PlayerListener
import com.nvvi9.ytaudio.ui.viewmodels.PlayerViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class PlayerFragment :
    Fragment(),
    Injectable {

    @Inject
    lateinit var playerViewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentPlayerBinding

    private var isUserSeeking = false

    private val playerViewModel by viewModels<PlayerViewModel> {
        playerViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPlayerBinding.inflate(inflater).apply {
        progressBar.run {
            onStopTracking = {
                playerViewModel.seekTo(
                    (it * (playerViewModel.nowPlayingInfo.value?.durationMillis ?: 0) / 100).toLong()
                )
            }
            onProgressChanged = { position, byUser ->
                if (byUser) {
                    playerViewModel.run {
                        updateTimePosition(
                            (position * (nowPlayingInfo.value?.durationMillis ?: 0) / 100).toLong()
                        )
                    }
                }
            }
        }
        listener = PlayerListener(
            { playerViewModel.playPause(it) },
            { playerViewModel.skipToNext() },
            { playerViewModel.skipToPrevious() },
            { playerViewModel.setRepeatMode() },
            { playerViewModel.setShuffleMode() }
        )
        viewModel = playerViewModel
    }.also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerViewModel.run {
            nowPlayingInfo.observe(viewLifecycleOwner) {
                binding.nowPlaying = it
                Log.i(javaClass.simpleName, it?.toString() ?: "empty")
            }

            currentButtonRes.observe(viewLifecycleOwner) {
                binding.buttonRes = it
            }

            currentPositionMillis.observe(viewLifecycleOwner) {
                if (!isUserSeeking) {
                    binding.run {
                        position = it?.toInt()
                        displayPosition = it?.toInt()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = PlayerFragment()
    }
}