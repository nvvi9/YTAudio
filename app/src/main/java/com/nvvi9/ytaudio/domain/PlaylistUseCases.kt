package com.nvvi9.ytaudio.domain

import androidx.lifecycle.Transformations
import com.nvvi9.ytaudio.repositories.PlaylistRepository
import com.nvvi9.ytaudio.utils.extensions.toMediaMetadataList
import com.nvvi9.ytaudio.vo.PlaylistItem
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlaylistUseCases @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : UseCases {

    fun getPlaylistItems() = Transformations.map(playlistRepository.getAudioInfo()) { list ->
        list.filter { it.needUpdate == false }.map { PlaylistItem.from(it) }
    }

    fun getItemsId() = Transformations.map(playlistRepository.getAudioInfo()) { audioInfo ->
        audioInfo.map { it.id }
    }

    fun getMediaMetadata() = Transformations.map(playlistRepository.getAudioInfo()) { list ->
        list.filter { it.needUpdate == false }.toMediaMetadataList()
    }

    fun getMetadata() = getMediaMetadata().value ?: emptyList()
}