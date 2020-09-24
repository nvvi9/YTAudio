package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvvi9.ytaudio.domain.PlaylistUseCases
import com.nvvi9.ytaudio.repositories.AudioInfoRepository
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytaudio.vo.PlaylistItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
class PlaylistViewModel
@Inject constructor(
    private val playlistUseCases: PlaylistUseCases,
    private val audioInfoRepository: AudioInfoRepository,
    audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    val playlistItems = playlistUseCases.getPlaylistItems()

    fun deleteFromDatabase(vararg items: PlaylistItem) {
        viewModelScope.launch {
            audioInfoRepository.deleteById(*items.map { it.id }.toTypedArray())
        }
    }

    val networkFailure = Transformations.map(audioServiceConnection.networkFailure) { it }
}
