package com.nvvi9.ytaudio.repositories

import android.util.Log
import com.nvvi9.YTStream
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo
import com.nvvi9.ytaudio.db.AudioInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class AudioInfoRepository @Inject constructor(
    private val ytStream: YTStream,
    private val audioInfoDao: AudioInfoDao
) : Repository {

    suspend fun insertIntoDatabase(vararg id: String) {
        ytStream.extractVideoData(*id)
            .filterNotNull()
            .map { AudioInfo.fromVideoData(it) }
            .filterNotNull()
            .collect {
                audioInfoDao.insert(it)
                Log.i("AudioInfoRepository", "Added to playlist: ${it.id}")
            }
    }

    suspend fun updateById(vararg id: String) {
        withContext(Dispatchers.IO) {
            ytStream.extractVideoData(*id)
                .toList()
                .filterNotNull()
                .mapNotNull { AudioInfo.fromVideoData(it) }
                .let { audioInfoDao.updatePlaylist(it) }
        }
    }

    suspend fun deleteById(vararg id: String) {
        withContext(Dispatchers.IO) {
            audioInfoDao.deleteById(*id)
        }
    }

    fun getAudioInfo() =
        audioInfoDao.getAllAudio()
}