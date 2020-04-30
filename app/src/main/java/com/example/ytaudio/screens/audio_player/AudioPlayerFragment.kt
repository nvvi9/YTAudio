package com.example.ytaudio.screens.audio_player

import android.media.AudioManager
import android.media.MediaPlayer
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.ytaudio.R
import com.example.ytaudio.databinding.AudioPlayerFragmentBinding

class AudioPlayerFragment : Fragment() {

    companion object {
        const val ONE_SECOND = 1000
    }

    private lateinit var binding: AudioPlayerFragmentBinding
    private lateinit var viewModel: AudioPlayerViewModel
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var runnable: Runnable
    private var handler = Handler()
    private var onPause = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val audioPlayerFragmentArgs by navArgs<AudioPlayerFragmentArgs>()

        binding =
            DataBindingUtil.inflate(inflater, R.layout.audio_player_fragment, container, false)

//        viewModel = ViewModelProviders.of(this).get(AudioPlayerViewModel::class.java)

        binding.apply {
            buttonPlay.setOnClickListener {
                it.visibility = View.INVISIBLE
                buttonPause.visibility = View.VISIBLE
                start()
            }
            buttonPause.setOnClickListener {
                it.visibility = View.INVISIBLE
                buttonPlay.visibility = View.VISIBLE
                pause()
            }
            seekbarAudio.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser)
                        mediaPlayer?.seekTo(progress * ONE_SECOND)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        stop()
    }

    private fun start() {
        if (onPause) {
            mediaPlayer?.apply {
                seekTo(this.currentPosition)
                start()
            }
        } else {
            mediaPlayer = MediaPlayer.create(this.context, R.raw.nothing_left).apply {
                isLooping = true
                start()
            }
        }
        onPause = false
        initializeSeekBar()
    }


    private fun pause() {
        if (mediaPlayer?.isPlaying!!) {
            mediaPlayer?.pause()
            onPause = true
        }
    }

    private fun stop() {
        mediaPlayer?.apply {
            stop()
            prepare()
        }
    }

    private fun initializeSeekBar() {
        binding.seekbarAudio.max = mediaPlayer?.duration!! / ONE_SECOND

        runnable = Runnable {
            binding.apply {
                seekbarAudio.progress = mediaPlayer?.currentPosition!! / ONE_SECOND
                currentTimeText.text =
                    DateUtils.formatElapsedTime((mediaPlayer?.currentPosition!! / ONE_SECOND).toLong())
                leftTimeText.text =
                    DateUtils.formatElapsedTime(((mediaPlayer?.duration!! - mediaPlayer?.currentPosition!!) / ONE_SECOND).toLong())
            }

            handler.postDelayed(runnable, ONE_SECOND.toLong())
        }
        handler.postDelayed(runnable, ONE_SECOND.toLong())
    }
}