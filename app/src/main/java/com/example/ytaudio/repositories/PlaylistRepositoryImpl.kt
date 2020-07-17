package com.example.ytaudio.repositories

import androidx.lifecycle.LiveData
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.entities.AudioInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlaylistRepositoryImpl @Inject constructor(private val databaseDao: AudioDatabaseDao) :
    PlaylistRepository {

    override val audioInfoList: LiveData<List<AudioInfo>>
        get() = databaseDao.getAllAudio()

    override suspend fun deleteAudioInfo(audioId: List<String>) {
        withContext(Dispatchers.IO) {
            databaseDao.delete(audioId)
        }
    }
}