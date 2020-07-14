package com.example.service.exo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.session.PlaybackState
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.service.AudioService
import com.example.service.domain.AudioItem
import com.google.android.exoplayer2.C.CONTENT_TYPE_MUSIC
import com.google.android.exoplayer2.C.USAGE_MEDIA
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes

class ExoPlayerManager(val context: Context) : ExoPlayerManagerCallback {

    private val exoPlayerEventListener = ExoPlayerEventListener()

    private val audioNoisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private val wifiLock: WifiManager.WifiLock =
        (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
            .createWifiLock(WifiManager.WIFI_MODE_FULL, "app-lock")

    private val audioManager: AudioManager =
        context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val audioAttributes =
        AudioAttributes.Builder()
            .setContentType(CONTENT_TYPE_MUSIC)
            .setUsage(USAGE_MEDIA)
            .build()

    private var audioStateCallback: ExoPlayerManagerCallback.AudioStateCallback? = null
    private var currentAudio: AudioItem? = null
    private var currentAudioFocusState = NO_FOCUS_NO_DUCK
    private var exoPlayer: SimpleExoPlayer? = null
    private var playOnFocusGain = false
    private var exoPlayerIsStopped = false
    private var noisyReceiverRegistered = false

    private val noisyReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY && isPlaying()) {
                val i = Intent(context, AudioService::class.java).apply {
                    action = AudioService.ACTION_CMD
                    putExtra(AudioService.CMD_NAME, AudioService.CMD_PAUSE)
                }
                context?.applicationContext?.startService(i)
            }
        }
    }

    private val progressHandler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            val duration = exoPlayer?.duration ?: 0
            val position = exoPlayer?.currentPosition ?: 0
            audioStateCallback?.setCurrentPosition(position, duration)
            sendEmptyMessageDelayed(0, 500L)
        }
    }

    private val onAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener {
        when (it) {
            AudioManager.AUDIOFOCUS_GAIN -> currentAudioFocusState = AUDIO_FOCUSED
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                currentAudioFocusState = NO_FOCUS_CAN_DUCK
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                currentAudioFocusState = NO_FOCUS_NO_DUCK
                playOnFocusGain = exoPlayer != null && exoPlayer?.playWhenReady ?: false
            }
            AudioManager.AUDIOFOCUS_LOSS -> currentAudioFocusState = NO_FOCUS_NO_DUCK
        }
        configurePlayerState()
    }

    private fun configurePlayerState() {
        if (currentAudioFocusState == NO_FOCUS_NO_DUCK) {
            pause()
        } else {
            registerNoisyReceiver()

            if (currentAudioFocusState == NO_FOCUS_NO_DUCK)
                exoPlayer?.volume = VOLUME_DUCK
            else
                exoPlayer?.volume = VOLUME_NORMAL
            if (playOnFocusGain) {
                exoPlayer?.playWhenReady = true
                playOnFocusGain = false
            }
        }
    }

    private fun registerNoisyReceiver() {
        if (!noisyReceiverRegistered) {
            context.applicationContext.registerReceiver(noisyReceiver, audioNoisyIntentFilter)
            noisyReceiverRegistered = true
        }
    }

    private fun unregisterNoisyReceiver() {
        if (noisyReceiverRegistered) {
            context.applicationContext.unregisterReceiver(noisyReceiver)
            noisyReceiverRegistered = false
        }
    }

    private fun giveUpAudioFocus() {
        if (audioManager.abandonAudioFocus(onAudioFocusChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            currentAudioFocusState = NO_FOCUS_NO_DUCK
        }
    }

    private fun releaseResources(releasePlayer: Boolean) {
        if (releasePlayer) {
            progressHandler.removeMessages(0)
            exoPlayer?.release()
            exoPlayer?.removeListener(exoPlayerEventListener)
            exoPlayer = null
            exoPlayerIsStopped = true
            playOnFocusGain = false
        }
    }

    private fun tryGetAudioFocus() {
        val result = audioManager.requestAudioFocus(
            onAudioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )

        currentAudioFocusState = if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            AUDIO_FOCUSED
        } else {
            NO_FOCUS_NO_DUCK
        }
    }

    override fun getCurrentAudioState(): Int {
        if (exoPlayer == null) {
            return if (exoPlayerIsStopped) {
                currentAudio?.isPlaying = false
                PlaybackState.STATE_STOPPED
            } else {
                currentAudio?.isPlaying = false
                PlaybackState.STATE_NONE
            }
        }
        return when (exoPlayer?.playbackState) {
            Player.STATE_IDLE -> {
                currentAudio?.isPlaying = false
                PlaybackState.STATE_PAUSED
            }
            Player.STATE_BUFFERING -> {
                currentAudio?.isPlaying = true
                PlaybackState.STATE_BUFFERING
            }
            Player.STATE_READY -> {
                if (exoPlayer?.playWhenReady == true) {
                    currentAudio?.isPlaying = true
                    PlaybackState.STATE_PLAYING
                } else {
                    currentAudio?.isPlaying = false
                    PlaybackState.STATE_PAUSED
                }
            }
            Player.STATE_ENDED -> {
                currentAudio?.isPlaying = false
                PlaybackState.STATE_STOPPED
            }
            else -> {
                currentAudio?.isPlaying = false
                PlaybackState.STATE_NONE
            }
        }
    }

    override fun isPlaying(): Boolean =
        playOnFocusGain || exoPlayer != null && exoPlayer?.playWhenReady == true

    override fun getCurrentStreamPosition(): Long =
        exoPlayer?.currentPosition ?: 0

    override fun getCurrentAudio(): AudioItem? =
        currentAudio

    override fun updateLastStreamPosition() = Unit

    override fun start() = Unit

    override fun stop() {
        giveUpAudioFocus()
        unregisterNoisyReceiver()
        releaseResources(true)
    }

    override fun play(audioItem: AudioItem) {
        playOnFocusGain = true
        tryGetAudioFocus()
        registerNoisyReceiver()
        val id = audioItem.id
        val isAudioChanged = id != currentAudio?.id

        if (isAudioChanged || exoPlayer == null) {
            releaseResources(false)
            if (exoPlayer == null) {
                exoPlayer = SimpleExoPlayer.Builder(context).build().also {
                    it.addListener(exoPlayerEventListener)
                    it.audioAttributes = audioAttributes
                }
            }
        }
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Long) {
        TODO("Not yet implemented")
    }

    override fun setCallback(callback: ExoPlayerManagerCallback.AudioStateCallback) {
        TODO("Not yet implemented")
    }

    private inner class ExoPlayerEventListener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        }
    }

    companion object {

        private const val VOLUME_DUCK = 0.2f
        private const val VOLUME_NORMAL = 1.0f
        private const val NO_FOCUS_NO_DUCK = 0
        private const val NO_FOCUS_CAN_DUCK = 1
        private const val AUDIO_FOCUSED = 2
    }
}