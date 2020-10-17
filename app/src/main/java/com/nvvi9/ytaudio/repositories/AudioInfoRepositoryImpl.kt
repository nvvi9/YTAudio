package com.nvvi9.ytaudio.repositories

import com.nvvi9.YTStream
import com.nvvi9.ytaudio.db.AudioInfoDao
import com.nvvi9.ytaudio.repositories.base.AudioInfoRepository
import com.nvvi9.ytaudio.repositories.mapper.AudioInfoMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class AudioInfoRepositoryImpl @Inject constructor(
    private val ytStream: YTStream,
    private val audioInfoDao: AudioInfoDao
) : AudioInfoRepository {

    override suspend fun addToPlaylist(vararg id: String) {
        ytStream.extractVideoData(*id)
            .filterNotNull()
            .mapNotNull { AudioInfoMapper.map(it) }
            .collect { audioInfoDao.insert(it) }
    }

    override suspend fun deleteFromPlaylist(vararg id: String) {
        audioInfoDao.deleteById(*id)
    }

    override fun getPlaylist() =
        audioInfoDao.getAllAudio()
}