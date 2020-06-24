package com.example.ytaudio.fragments

import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ytaudio.R
import com.example.ytaudio.databinding.AudioPlayerFragmentBinding
import com.example.ytaudio.utils.FactoryUtils
import com.example.ytaudio.viewmodels.AudioPlayerViewModel
import com.example.ytaudio.viewmodels.MainActivityViewModel

class AudioPlayerFragment : Fragment() {

    private lateinit var binding: AudioPlayerFragmentBinding
    private lateinit var audioPlayerViewModel: AudioPlayerViewModel
    private lateinit var mainActivityViewModel: MainActivityViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil
            .inflate(inflater, R.layout.audio_player_fragment, container, false)

        val application = requireNotNull(activity).application

        audioPlayerViewModel =
            ViewModelProvider(this, FactoryUtils.provideAudioPlayerViewModel(application))
                .get(AudioPlayerViewModel::class.java)

        mainActivityViewModel =
            ViewModelProvider(this, FactoryUtils.provideMainActivityViewModel(application))
                .get(MainActivityViewModel::class.java)

        audioPlayerViewModel.apply {
            currentAudioInfo.observe(viewLifecycleOwner, Observer { updateUI(it) })

            audioButtonRes.observe(
                viewLifecycleOwner,
                Observer { binding.audioButton.setImageResource(it) })

            audioPosition.observe(
                viewLifecycleOwner,
                Observer { binding.currentTimeText.text = DateUtils.formatElapsedTime(it / 1000) })
        }

        binding.audioButton.setOnClickListener {
            audioPlayerViewModel.currentAudioInfo.value?.let {
                mainActivityViewModel.playAudio(it.audioId)
            }
        }

        return binding.root
    }

    private fun updateUI(currentAudioInfo: AudioPlayerViewModel.NowPlayingAudioInfo) {
        binding.apply {
            if (currentAudioInfo.thumbnailUri == Uri.EMPTY) {
                thumbnail.setImageResource(R.drawable.ic_album_black)
            } else {
                Glide.with(this@AudioPlayerFragment).load(currentAudioInfo.thumbnailUri)
                    .into(thumbnail)
            }
            durationText.text = currentAudioInfo.duration
            textTitle.text = currentAudioInfo.title
            textAuthor.text = currentAudioInfo.subtitle
        }
    }
}