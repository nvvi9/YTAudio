package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.*
import com.nvvi9.ytaudio.domain.AudioInfoUseCase
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.utils.extensions.id
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

    override fun onCleared() {
        super.onCleared()
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

    fun deleteFromDatabase(vararg items: PlaylistItem) {
        viewModelScope.launch(ioDispatcher) {
            audioInfoUseCase.deleteFromPlaylist(*items.map { it.id }.toTypedArray())
        }
    }
}
