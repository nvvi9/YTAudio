package com.example.ytaudio.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ytaudio.R
import com.example.ytaudio.databinding.AudioPlayerFragmentBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.viewmodels.MainActivityViewModel
import com.example.ytaudio.ui.viewmodels.PlayerViewModel
import javax.inject.Inject

class PlayerFragment : Fragment(), Injectable {

    @Inject
    lateinit var mainActivityViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var playerViewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: AudioPlayerFragmentBinding

    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        mainActivityViewModelFactory
    }

    private val playerViewModel: PlayerViewModel by viewModels {
        playerViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AudioPlayerFragmentBinding.inflate(inflater)

        playerViewModel.apply {
            currentAudioInfo.observe(viewLifecycleOwner, Observer { updateUI(it) })

            audioButtonRes.observe(
                viewLifecycleOwner,
                Observer { binding.audioButton.setImageResource(it) })

            audioPosition.observe(
                viewLifecycleOwner,
                Observer { binding.currentTimeText.text = DateUtils.formatElapsedTime(it / 1000) })
        }

        binding.audioButton.setOnClickListener {
            playerViewModel.currentAudioInfo.value?.let {
                mainActivityViewModel.playAudio(it.audioId)
            }
        }

        return binding.root
    }

    private fun updateUI(currentAudioInfo: PlayerViewModel.NowPlayingAudioInfo) {
        binding.apply {
            if (currentAudioInfo.thumbnailUri == Uri.EMPTY) {
                thumbnail.setImageResource(R.drawable.ic_album_black)
            } else {
                Glide.with(this@PlayerFragment).load(currentAudioInfo.thumbnailUri)
                    .into(thumbnail)
            }
            durationText.text = currentAudioInfo.duration
            textTitle.text = currentAudioInfo.title
            textAuthor.text = currentAudioInfo.subtitle
        }
    }
}