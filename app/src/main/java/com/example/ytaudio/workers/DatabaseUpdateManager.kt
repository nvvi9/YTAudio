package com.example.ytaudio.workers

import com.example.ytaudio.db.PlaylistDao
import com.example.ytaudio.network.YTExtractor
import com.example.ytaudio.utils.extensions.mapParallel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class DatabaseUpdateManager @Inject constructor(
    private val ytExtractor: YTExtractor,
    private val databaseDao: PlaylistDao,
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun fullRefresh() {
        withContext(dispatcher) {
            val audioInfoList = databaseDao.getAllAudioInfo()
            databaseDao.update(audioInfoList.mapParallel(dispatcher) {
                ytExtractor.extractAudioInfo(it.youtubeId)
            })
        }
    }
}