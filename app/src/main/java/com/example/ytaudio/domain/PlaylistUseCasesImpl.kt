package com.example.ytaudio.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ytaudio.repositories.base.PlaylistRepository
import com.example.ytaudio.vo.PlaylistItem
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlaylistUseCasesImpl @Inject constructor(private val playlistRepository: PlaylistRepository) :
    PlaylistUseCases {

    override val playlistItems: LiveData<List<PlaylistItem>>
        get() = Transformations.distinctUntilChanged(
            Transformations.map(playlistRepository.audioInfoList) { list ->
                list.filterNot { it.needUpdate }.map { PlaylistItem.from(it) }
            })

    override suspend fun deleteItems(items: List<PlaylistItem>) {
        playlistRepository.deleteAudioInfo(items.map { it.id })
    }
}