package com.nvvi9.ytaudio.domain

import androidx.lifecycle.map
import com.nvvi9.ytaudio.domain.mapper.MediaMetadataMapper
import com.nvvi9.ytaudio.domain.mapper.PlaylistItemMapper
import com.nvvi9.ytaudio.repositories.base.AudioInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class AudioInfoUseCase @Inject constructor(
    private val audioInfoRepository: AudioInfoRepository
) {

    fun getPlaylistItems() =
        audioInfoRepository.getPlaylist().map { list ->
            list.filter { it.needUpdate == false }
                .map { PlaylistItemMapper.map(it) }
        }

    fun getItemsId() =
        audioInfoRepository.getPlaylist()
            .map { audioInfo -> audioInfo.map { it.id } }

    fun getMediaMetadata() =
        audioInfoRepository.getPlaylist().map { list ->
            list.filter { it.needUpdate == false }
                .map { MediaMetadataMapper.map(it) }
        }

    suspend fun addToPlaylist(id: String) =
        audioInfoRepository.addToPlaylist(id)

    suspend fun deleteFromPlaylist(id: String) =
        audioInfoRepository.deleteFromPlaylist(id)
}