package com.nvvi9.ytaudio.domain

import androidx.lifecycle.map
import com.nvvi9.ytaudio.repositories.AudioInfoRepository
import com.nvvi9.ytaudio.utils.extensions.toMediaMetadataList
import com.nvvi9.ytaudio.vo.PlaylistItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class AudioInfoUseCases @Inject constructor(
    private val audioInfoRepository: AudioInfoRepository
) {

    fun getPlaylistItems() =
        audioInfoRepository.getAudioInfo().map { list ->
            list.filter { it.needUpdate == false }
                .map { PlaylistItem.from(it) }
        }

    fun getItemsId() =
        audioInfoRepository.getAudioInfo()
            .map { audioInfo ->
                audioInfo.map { it.id }
            }

    fun getMediaMetadata() =
        audioInfoRepository.getAudioInfo().map { list ->
            list.filter { it.needUpdate == false }
                .toMediaMetadataList()
        }

    fun getMetadata() =
        getMediaMetadata().value ?: emptyList()

    suspend fun addToPlaylist(id: String) {
        audioInfoRepository.insertIntoDatabase(id)
    }

    suspend fun deleteFromPlaylist(id: String) {
        audioInfoRepository.deleteById(id)
    }
}