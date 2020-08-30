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
import com.bumptech.glide.request.RequestOptions
import com.example.ytaudio.R
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.viewmodels.MainActivityViewModel
import com.example.ytaudio.ui.viewmodels.PlayerViewModel
import kotlinx.android.synthetic.main.fragment_play.*
import javax.inject.Inject


class PlayFragment : Fragment(), Injectable {

    @Inject
    lateinit var mainActivityViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var playerViewModelFactory: ViewModelProvider.Factory

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
    ): View? = inflater.inflate(R.layout.fragment_play, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerViewModel.apply {
            currentAudioInfo.observe(viewLifecycleOwner, Observer {
                updateUI(it)
            })

            audioButtonRes.observe(viewLifecycleOwner, Observer {
                play_pause_button.setImageResource(it)
            })

            audioPosition.observe(viewLifecycleOwner, Observer {
                current_progress.text = DateUtils.formatElapsedTime(it / 1000)
            })

        }

        play_pause_button.setOnClickListener {
            playerViewModel.currentAudioInfo.value?.let {
                mainActivityViewModel.playAudio(it.audioId)
            }
        }
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