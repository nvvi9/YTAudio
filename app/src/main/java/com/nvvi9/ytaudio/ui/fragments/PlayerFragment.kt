package com.nvvi9.ytaudio.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.nvvi9.ytaudio.databinding.FragmentPlayerBinding
import com.nvvi9.ytaudio.di.Injectable
import com.nvvi9.ytaudio.ui.MainActivity
import com.nvvi9.ytaudio.ui.viewmodels.MainActivityViewModel
import com.nvvi9.ytaudio.ui.viewmodels.PlayerViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import kotlin.math.abs


@FlowPreview
@ExperimentalCoroutinesApi
class PlayerFragment :
    Fragment(),
    MotionLayout.TransitionListener,
    Injectable {

    @Inject
    lateinit var mainActivityViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var playerViewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentPlayerBinding

    private var isUserSeeking = false

    private val mainActivityViewModel by viewModels<MainActivityViewModel> {
        mainActivityViewModelFactory
    }

    private val playerViewModel by viewModels<PlayerViewModel> {
        playerViewModelFactory
    }

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {

        private var userSelectedPosition: Int? = null

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            userSelectedPosition = progress.takeIf { fromUser }
            binding.displayPosition = progress
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            isUserSeeking = true
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            isUserSeeking = false
            userSelectedPosition?.let {
                mainActivityViewModel.seekTo(it.toLong())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPlayerBinding.inflate(inflater).apply {
        playPauseButton.setOnClickListener {
            playerViewModel.currentAudioInfo.value?.id?.let {
                mainActivityViewModel.playAudio(it)
            }
        }
        progressBar.setOnSeekBarChangeListener(seekBarChangeListener)

        motionLayout.setTransitionListener(this@PlayerFragment)
        viewModel = playerViewModel
    }.also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerViewModel.run {
            currentAudioInfo.observe(viewLifecycleOwner) {
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

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
        (activity as MainActivity).main_motion_layout.progress = abs(p3)
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

    companion object {
        fun newInstance() = PlayerFragment()
    }
}