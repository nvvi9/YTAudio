package com.example.ytaudio.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ytaudio.R
import com.example.ytaudio.databinding.FragmentPlayBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.MainActivity
import com.example.ytaudio.ui.viewmodels.MainActivityViewModel
import com.example.ytaudio.ui.viewmodels.PlayerViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_play.*
import javax.inject.Inject
import kotlin.math.abs


class PlayFragment : Fragment(), MotionLayout.TransitionListener, Injectable {

    @Inject
    lateinit var mainActivityViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var playerViewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentPlayBinding

    private val mainActivityViewModel by viewModels<MainActivityViewModel> {
        mainActivityViewModelFactory
    }

    private val playerViewModel by viewModels<PlayerViewModel> {
        playerViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        FragmentPlayBinding.inflate(inflater).apply {
            playPauseButton.setOnClickListener {
                playerViewModel.currentAudioInfo.value?.let {
                    mainActivityViewModel.playAudio(it.audioId)
                }
            }

            motionLayout.setTransitionListener(this@PlayFragment)
        }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerViewModel.apply {
            currentAudioInfo.observe(viewLifecycleOwner) {
                updateUI(it)
            }

            audioButtonRes.observe(viewLifecycleOwner) {
                binding.playPauseButton.setImageResource(it)
            }

            audioPosition.observe(viewLifecycleOwner) {
                binding.currentProgress.text = DateUtils.formatElapsedTime(it / 1000)
            }
        }
    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
        (activity as MainActivity).main_motion_layout.progress = abs(p3)
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}

    override fun onTransitionTrigger(
        p0: MotionLayout?,
        p1: Int,
        p2: Boolean,
        p3: Float
    ) {
    }

    private fun updateUI(currentAudioInfo: PlayerViewModel.NowPlayingAudioInfo) {
        if (currentAudioInfo.thumbnailUri == Uri.EMPTY) {
            thumbnail.setImageResource(R.drawable.ic_album_black)
        } else {
            Glide.with(this@PlayFragment).load(currentAudioInfo.thumbnailUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_notification)
                        .error(R.drawable.ic_notification)
                ).into(thumbnail)
        }
        duration.text = currentAudioInfo.duration
    }

    companion object {
        fun newInstance() = PlayFragment()
    }
}