package com.example.ytaudio.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.ytaudio.AudioItem
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.fragments.AudioPlayerFragment
import com.example.ytaudio.service.MediaPlaybackServiceConnection
import com.example.ytaudio.service.extensions.id
import com.example.ytaudio.service.extensions.isPlayEnabled
import com.example.ytaudio.service.extensions.isPlaying
import com.example.ytaudio.service.extensions.isPrepared
import com.example.ytaudio.utils.*
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val database: AudioDatabaseDao,
    private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val rootMediaId: LiveData<String> =
        Transformations.map(mediaPlaybackServiceConnection.isConnected) {
            if (it) {
                mediaPlaybackServiceConnection.rootMediaId
            } else {
                null
            }
        }

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

    private fun playAudio(audioItem: AudioItem, pauseAllowed: Boolean) {
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

    val needUpdateAudioInfoList = Transformations.map(database.getAllAudio()) { list ->
        list?.filter {
            it.needUpdate
        }
    }

    private val nowUpdatingAudioInfoSet = mutableSetOf<AudioInfo>()

    fun updateAudioInfoList(audioInfoList: List<AudioInfo>) {
        if (nowUpdatingAudioInfoSet.addAll(audioInfoList)) {
            uiScope.launch {
                try {
                    val startTime = System.currentTimeMillis()
                    audioInfoList.forEachParallel {
                        it.updateInfo()
                    }
                    database.update(audioInfoList)
                    nowUpdatingAudioInfoSet.removeAll(audioInfoList)
                    Log.i(
                        LOG_TAG,
                        "${audioInfoList.size} items updated in ${System.currentTimeMillis() - startTime} ms"
                    )
                } catch (e: ExtractionException) {
                    showToast("Extraction failed")
                } catch (e: YoutubeRequestException) {
                    showToast("Check your connection")
                } catch (e: Exception) {
                    showToast()
                }
            }
        }
    }

    fun onExtract(youtubeUrl: String) {
        uiScope.launch {
            try {
                val startTime = System.currentTimeMillis()
                val audioInfo =
                    getAudioInfo(youtubeUrl.takeLastWhile { it != '=' && it != '/' })
                database.insert(audioInfo)
                Log.i(
                    LOG_TAG,
                    "${audioInfo.audioTitle} added in ${System.currentTimeMillis() - startTime} ms"
                )
            } catch (e: ExtractionException) {
                showToast("Extraction failed")
            } catch (e: YoutubeRequestException) {
                showToast("Check your connection")
            } catch (e: LiveContentException) {
                showToast(e.message)
            } catch (e: Exception) {
                showToast()
            }
        }
    }

    private fun showToast(message: String? = null) =
        Toast.makeText(
            mediaPlaybackServiceConnection.context,
            message ?: "Unknown error",
            Toast.LENGTH_SHORT
        ).show()

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }


    class Factory(
        private val databaseDao: AudioDatabaseDao,
        private val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection
    ) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(databaseDao, mediaPlaybackServiceConnection) as T
        }
    }
}

data class FragmentNavigationRequest(
    val fragment: Fragment,
    val addToBackStack: Boolean = false,
    val tag: String? = null
)

private const val LOG_TAG = "MainActivityViewModel"