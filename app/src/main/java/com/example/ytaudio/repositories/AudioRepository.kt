package com.example.ytaudio.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.domain.PlaylistItem
import com.example.ytaudio.utils.extensions.mapParallel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AudioRepository(context: Context) : BaseRepository(context) {

    val audioInfoList = databaseDao.getAllAudio()

    val playlistItems: LiveData<List<PlaylistItem>> =
        Transformations.map(databaseDao.getAllAudio()) { list ->
            list?.map { PlaylistItem.from(it) }
        }

    suspend fun updateAllAudioInfo() {
        withContext(Dispatchers.IO) {
            val startTime = System.nanoTime()
            val audioInfoList =
                databaseDao.getAllAudioInfo().mapParallel(Dispatchers.IO) {
                    extractAudioInfo(it.youtubeId)
                }.filterNotNull()
            databaseDao.update(audioInfoList)
            Log.i(
                javaClass.simpleName,
                "Full database update in ${(System.nanoTime() - startTime) / 1e6} ms"
            )
        }
    }

    suspend fun updateAudioInfoList(audioList: List<AudioInfo>) {
        withContext(Dispatchers.IO) {
            val startTime = System.nanoTime()
            val list = audioList.mapParallel(Dispatchers.IO) {
                extractAudioInfo(it.youtubeId)
            }.filterNotNull()
            databaseDao.update(list)
            Log.i(
                javaClass.simpleName,
                "${audioList.size} items updated in ${(System.nanoTime() - startTime) / 1e6} ms"
            )
        }
    }

    suspend fun deleteFromDatabase(idList: List<String>) {
        withContext(Dispatchers.IO) {
            databaseDao.delete(idList)
        }
    }
}