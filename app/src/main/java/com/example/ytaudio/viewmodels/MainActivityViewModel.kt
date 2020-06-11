package com.example.ytaudio.viewmodels

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.ytaudio.AudioItem
import com.example.ytaudio.fragments.AudioPlayerFragment
import com.example.ytaudio.service.MediaPlaybackServiceConnection
import com.example.ytaudio.service.extensions.id
import com.example.ytaudio.service.extensions.isPlayEnabled
import com.example.ytaudio.service.extensions.isPlaying
import com.example.ytaudio.service.extensions.isPrepared
import com.example.ytaudio.utils.Event

class MainActivityViewModel(
    private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection
) : ViewModel() {

    val rootMediaId: LiveData<String> =
        Transformations.map(mediaPlaybackServiceConnection.isConnected) {
            if (it) {
                mediaPlaybackServiceConnection.rootMediaId
            } else {
                null
            }
        }

    private val _navigateToPlaylist = MutableLiveData<Event<String>>()
    val navigateToPlaylist: LiveData<Event<String>>
        get() = _navigateToPlaylist

    private val _navigateToFragment = MutableLiveData<Event<FragmentNavigationRequest>>()
    val navigateToFragment: LiveData<Event<FragmentNavigationRequest>>
        get() = _navigateToFragment

    fun audioItemClicked(audioItem: AudioItem) {
        playAudio(audioItem, false)
        showFragment(AudioPlayerFragment.getInstance())
    }

    fun showFragment(fragment: Fragment, addToBackStack: Boolean = true, tag: String? = null) {
        _navigateToFragment.value = Event(FragmentNavigationRequest(fragment, addToBackStack, tag))
    }

    fun playAudio(audioItem: AudioItem, pauseAllowed: Boolean) {
        val nowPlaying = mediaPlaybackServiceConnection.nowPlaying.value
        val transportControls = mediaPlaybackServiceConnection.transportControls

        val isPrepared = mediaPlaybackServiceConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && audioItem.audioId == nowPlaying?.id) {
            mediaPlaybackServiceConnection.playbackState.value?.let {
                when {
                    it.isPlaying -> if (pauseAllowed) transportControls.pause() else Unit
                    it.isPlayEnabled -> transportControls.play()
                }
            }
        } else {
            transportControls.playFromMediaId(audioItem.audioId, null)
        }
    }

    fun playAudio(audioId: String) {
        val nowPlaying = mediaPlaybackServiceConnection.nowPlaying.value
        val transportControls = mediaPlaybackServiceConnection.transportControls

        val isPrepared = mediaPlaybackServiceConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && audioId == nowPlaying?.id) {
            mediaPlaybackServiceConnection.playbackState.value?.let {
                when {
                    it.isPlaying -> transportControls.pause()
                    it.isPlayEnabled -> transportControls.play()
                }
            }
        } else {
            transportControls.playFromMediaId(audioId, null)
        }
    }

    class Factory(private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(mediaPlaybackServiceConnection) as T
        }
    }
}

data class FragmentNavigationRequest(
    val fragment: Fragment,
    val addToBackStack: Boolean = false,
    val tag: String? = null
)