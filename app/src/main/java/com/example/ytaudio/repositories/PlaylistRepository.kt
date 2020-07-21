package com.example.ytaudio.repositories

import androidx.lifecycle.LiveData
import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.db.PlaylistDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


interface PlaylistRepository {

    val audioInfoList: LiveData<List<AudioInfo>>

    suspend fun deleteAudioInfo(audioId: List<String>)
}


@Singleton
class PlaylistRepositoryImpl @Inject constructor(private val databaseDao: PlaylistDao) :
    PlaylistRepository {

    override val audioInfoList: LiveData<List<AudioInfo>>
        get() = databaseDao.getAllAudio()

    override suspend fun deleteAudioInfo(audioId: List<String>) {
        withContext(Dispatchers.IO) {
            databaseDao.deleteById(audioId)
        }
    }
}