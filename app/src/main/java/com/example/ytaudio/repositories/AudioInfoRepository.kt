package com.example.ytaudio.repositories

import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.db.AudioInfoDao
import com.example.ytaudio.network.YTStreamApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AudioInfoRepository @Inject constructor(
    private val ytStreamApiService: YTStreamApiService,
    private val audioInfoDao: AudioInfoDao
) : Repository {

    suspend fun insertIntoDatabase(id: String) {
        withContext(Dispatchers.IO) {
            ytStreamApiService.getVideoData(id).firstOrNull()?.let {
                audioInfoDao.insert(AudioInfo.fromVideoData(it))
            }
        }
    }

    suspend fun updateAll() {
        withContext(Dispatchers.IO) {
            audioInfoDao.getAllAudioInfo().let { audioInfo ->
                ytStreamApiService.getVideoData(audioInfo.joinToString("+") { it.id })
            }.let { videoData ->
                audioInfoDao.updatePlaylist(videoData.map { AudioInfo.fromVideoData(it) })
            }
        }
    }

    suspend fun updateById(vararg id: String) {
        withContext(Dispatchers.IO) {
            ytStreamApiService.getVideoData(id.joinToString("+")).let { videoData ->
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