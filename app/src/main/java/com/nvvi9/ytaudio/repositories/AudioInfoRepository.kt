package com.nvvi9.ytaudio.repositories

import com.nvvi9.ytaudio.data.audioinfo.AudioInfo
import com.nvvi9.ytaudio.db.AudioInfoDao
import com.nvvi9.ytstream.YTStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@FlowPreview
@ExperimentalCoroutinesApi
class AudioInfoRepository @Inject constructor(
    private val ytStream: YTStream,
    private val audioInfoDao: AudioInfoDao
) : Repository {

    suspend fun insertIntoDatabase(id: String) {
        withContext(Dispatchers.IO) {
            ytStream.extractVideoData(id).collect {
                it?.let {
                    audioInfoDao.insert(AudioInfo.fromVideoData(it))
                }
            }
        }
    }

    suspend fun updateAll() {
        withContext(Dispatchers.IO) {
            audioInfoDao.getAllAudioInfo().let { audioInfo ->
                ytStream.extractVideoData(*audioInfo.map { it.id }.toTypedArray())
                    .toList()
                    .filterNotNull()
            }.let { videoData ->
                audioInfoDao.updatePlaylist(videoData.map { AudioInfo.fromVideoData(it) })
            }
        }
    }

    suspend fun updateById(vararg id: String) {
        withContext(Dispatchers.IO) {
            ytStream.extractVideoData(*id).toList().filterNotNull().let { videoData ->
                audioInfoDao.updatePlaylist(videoData.map {
                    AudioInfo.fromVideoData(it)
                })
            }
        }
    }

    suspend fun deleteById(vararg id: String) {
        withContext(Dispatchers.IO) {
            audioInfoDao.deleteById(*id)
        }
    }
}