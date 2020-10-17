package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.*
import com.nvvi9.ytaudio.domain.AudioInfoUseCase
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.utils.Event
import com.nvvi9.ytaudio.utils.extensions.id
import com.nvvi9.ytaudio.utils.extensions.isPlayEnabled
import com.nvvi9.ytaudio.utils.extensions.isPrepared
import com.nvvi9.ytaudio.vo.PlaylistItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class PlaylistViewModel @Inject constructor(
    private val audioInfoUseCase: AudioInfoUseCase,
    private val audioServiceConnection: AudioServiceConnection,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val playlistItems = audioInfoUseCase.getPlaylistItems()

    private val items = MediatorLiveData<List<PlaylistItem>>()

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    override fun onCleared() {
        super.onCleared()
        removeSources()
    }

    fun removeSources() {
        items.run {
            removeSource(playlistItems)
            removeSource(audioServiceConnection.nowPlaying)
        }
    }

    fun observeOnPlaylistItems(owner: LifecycleOwner, observer: Observer<List<PlaylistItem>>) {
        items.observe(owner, observer)

        items.addSource(playlistItems) {
            it?.let { items.postValue(it) }
        }

        items.addSource(audioServiceConnection.nowPlaying) { nowPlaying ->
            playlistItems.value?.map {
                it.isPlayingNow = nowPlaying.id == it.id
                it
            }?.let {
                items.postValue(it)
            }
        }
    }

    fun deleteFromPlaylist(item: PlaylistItem) {
        viewModelScope.launch(ioDispatcher) {
            if (!audioInfoUseCase.deleteFromPlaylist(item.id)) {
                _errorEvent.postValue(Event("Can't delete ${item.title}"))
            }
        }
    }

    fun playAudio(item: PlaylistItem) {
        audioServiceConnection.run {
            playbackState.value?.let {
                if (it.isPrepared && it.isPlayEnabled && item.id == nowPlaying.value?.id) {
                    transportControls.play()
                } else {
                    transportControls.playFromMediaId(item.id, null)
                }
            }
        }
    }
}
