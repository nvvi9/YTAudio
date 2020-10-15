package com.nvvi9.ytaudio.domain

import androidx.lifecycle.map
import com.nvvi9.ytaudio.repositories.base.AudioInfoRepository
import com.nvvi9.ytaudio.utils.extensions.toMediaMetadataList
import com.nvvi9.ytaudio.vo.PlaylistItem
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
                .map { PlaylistItem.from(it) }
        }

    fun getItemsId() =
        audioInfoRepository.getPlaylist()
            .map { audioInfo -> audioInfo.map { it.id } }

    fun getMediaMetadata() =
        audioInfoRepository.getPlaylist().map { list ->
            list.filter { it.needUpdate == false }
                .toMediaMetadataList()
        }

    fun getMetadata() =
        getMediaMetadata().value ?: emptyList()

    suspend fun addToPlaylist(vararg id: String) {
        audioInfoRepository.addToPlaylist(*id)
    }

    suspend fun deleteFromPlaylist(vararg id: String) {
        audioInfoRepository.deleteFromPlaylist(*id)
    }
}