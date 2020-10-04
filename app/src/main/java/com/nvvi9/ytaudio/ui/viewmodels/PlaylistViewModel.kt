package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvvi9.ytaudio.domain.AudioInfoUseCases
import com.nvvi9.ytaudio.repositories.AudioInfoRepository
import com.nvvi9.ytaudio.vo.PlaylistItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class PlaylistViewModel @Inject constructor(
    private val audioInfoRepository: AudioInfoRepository,
    audioInfoUseCases: AudioInfoUseCases,
) : ViewModel() {

    val playlistItems = audioInfoUseCases.getPlaylistItems()

    fun deleteFromDatabase(vararg items: PlaylistItem) {
        viewModelScope.launch {
            audioInfoRepository.deleteById(*items.map { it.id }.toTypedArray())
        }
    }
}
