package com.nvvi9.ytaudio.repositories

import com.nvvi9.ytaudio.db.RoomDataSource
import com.nvvi9.ytaudio.network.YTStreamDataSource
import com.nvvi9.ytaudio.repositories.base.AudioInfoRepository
import com.nvvi9.ytaudio.repositories.mapper.AudioInfoMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class AudioInfoRepositoryImpl @Inject constructor(
    private val ytStreamDataSource: YTStreamDataSource,
    private val roomDataSource: RoomDataSource
) : AudioInfoRepository {

    override suspend fun addToPlaylist(id: String): Boolean =
        ytStreamDataSource.extractVideoData(id)
            ?.let { AudioInfoMapper.map(it) }
            ?.let { roomDataSource.addToDatabase(it) } ?: false

    override suspend fun deleteFromPlaylist(id: String) =
        roomDataSource.deleteFromDatabase(id)

    override fun getPlaylist() =
        roomDataSource.getData()
}