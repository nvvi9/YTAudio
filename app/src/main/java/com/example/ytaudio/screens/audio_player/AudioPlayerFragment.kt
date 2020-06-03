package com.example.ytaudio.screens.audio_player

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabase
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
    private lateinit var audioUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val audioPlayerFragmentArgs by navArgs<AudioPlayerFragmentArgs>()

        binding =
            DataBindingUtil.inflate(inflater, R.layout.audio_player_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = AudioDatabase.getInstance(application).audioDatabaseDao
        val viewModelFactory = AudioPlayerViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AudioPlayerViewModel::class.java)

        viewModel.getAudioInfoFromDatabase(audioPlayerFragmentArgs.audioId)

        viewModel.isAudioInfoInitialized.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.audioInfo?.let {
                    binding.run {
                        textTitle.text = it.audioTitle
                        Glide.with(this@AudioPlayerFragment).load(it.photoUrl).into(photo)
                        audioUri = Uri.parse(it.audioUrl)
                        leftTimeText.text = DateUtils.formatElapsedTime(it.audioDurationSeconds)
                        viewModel.initializationDone()
                        buttonPause.isClickable = true
                        buttonPlay.isClickable = true
                    }
                }
            }
        })

        binding.apply {
            buttonPlay.setOnClickListener {
                it.visibility = View.INVISIBLE
                buttonPause.visibility = View.VISIBLE
                textTitle.ellipsize = TextUtils.TruncateAt.END
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

            lifecycleOwner = this@AudioPlayerFragment
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        stop()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun start() {
        if (onPause) {
            mediaPlayer?.apply {
                seekTo(this.currentPosition)
                start()
            }
        } else {
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(this@AudioPlayerFragment.context!!, audioUri)
                prepare()
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