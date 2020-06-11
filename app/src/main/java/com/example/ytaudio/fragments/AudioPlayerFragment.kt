package com.example.ytaudio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.R
import com.example.ytaudio.databinding.AudioPlayerFragmentBinding
import com.example.ytaudio.utils.FactoryUtils
import com.example.ytaudio.viewmodels.AudioPlayerViewModel

class AudioPlayerFragment : Fragment() {

    companion object {
        fun getInstance() = AudioPlayerFragment()
    }

    private lateinit var binding: AudioPlayerFragmentBinding
    private lateinit var audioPlayerViewModel: AudioPlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.audio_player_fragment, container, false)

        val application = requireNotNull(this.activity).application

        audioPlayerViewModel =
            ViewModelProvider(this, FactoryUtils.provideAudioPlayerViewModel(application)).get(
                AudioPlayerViewModel::class.java
            )

        return binding.root
    }
}