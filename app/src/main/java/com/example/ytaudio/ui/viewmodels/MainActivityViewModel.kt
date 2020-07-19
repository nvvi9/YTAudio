package com.example.ytaudio.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ytaudio.repositories.PlaylistRepositoryImpl
import com.example.ytaudio.service.AudioServiceConnection
import com.example.ytaudio.utils.extensions.id
import com.example.ytaudio.utils.extensions.isPlayEnabled
import com.example.ytaudio.utils.extensions.isPlaying
import com.example.ytaudio.utils.extensions.isPrepared
import javax.inject.Inject


class MainActivityViewModel @Inject constructor(
    private val repositoryImpl: PlaylistRepositoryImpl,
    private val audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    fun audioItemClicked(audioId: String) {
        playAudio(audioId, false)
    }

    private fun playAudio(audioId: String, pauseAllowed: Boolean) {
        val nowPlaying = audioServiceConnection.nowPlaying.value
        val transportControls = audioServiceConnection.transportControls

        val isPrepared = audioServiceConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && audioId == nowPlaying?.id) {
            audioServiceConnection.playbackState.value?.let {
                when {
                    it.isPlaying -> if (pauseAllowed) transportControls.pause() else Unit
                    it.isPlayEnabled -> transportControls.play()
                }
            }
        } else {
            transportControls.playFromMediaId(audioId, null)
        }
    }

    fun playAudio(audioId: String) {
        val nowPlaying = audioServiceConnection.nowPlaying.value
        val transportControls = audioServiceConnection.transportControls

        val isPrepared = audioServiceConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && audioId == nowPlaying?.id) {
            audioServiceConnection.playbackState.value?.let {
                when {
                    it.isPlaying -> transportControls.pause()
                    it.isPlayEnabled -> transportControls.play()
                }
            }
        } else {
            transportControls.playFromMediaId(audioId, null)
        }
    }
}