package com.example.ytaudio.domain

import androidx.lifecycle.Transformations
import com.example.ytaudio.repositories.PlaylistRepository
import com.example.ytaudio.utils.extensions.toMediaMetadataList
import com.example.ytaudio.vo.PlaylistItem
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlaylistUseCases @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : UseCases {

    fun getPlaylistItems() = Transformations.map(playlistRepository.getAudioInfo()) { list ->
        list.map { PlaylistItem.from(it) }
    }

    fun getMediaMetadata() = Transformations.map(playlistRepository.getAudioInfo()) {
        it.toMediaMetadataList()
    }

    fun getMetadata() = getMediaMetadata().value ?: emptyList()
}