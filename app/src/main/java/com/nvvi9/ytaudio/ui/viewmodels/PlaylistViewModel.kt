package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.*
import com.nvvi9.ytaudio.domain.AudioInfoUseCases
import com.nvvi9.ytaudio.repositories.AudioInfoRepository
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.utils.extensions.id
import com.nvvi9.ytaudio.vo.PlaylistItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class PlaylistViewModel @Inject constructor(
    private val audioInfoRepository: AudioInfoRepository,
    private val audioServiceConnection: AudioServiceConnection,
    audioInfoUseCases: AudioInfoUseCases,
) : ViewModel() {

    private val playlistItems = audioInfoUseCases.getPlaylistItems()

    private val items = MediatorLiveData<List<PlaylistItem>>()

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

    fun removeSources() {
        items.run {
            removeSource(playlistItems)
            removeSource(audioServiceConnection.nowPlaying)
        }
    }

    fun deleteFromDatabase(vararg items: PlaylistItem) {
        viewModelScope.launch {
            audioInfoRepository.deleteById(*items.map { it.id }.toTypedArray())
        }
    }
}
