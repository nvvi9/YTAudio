package com.example.ytaudio.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.ytaudio.db.PlaylistDao
import javax.inject.Inject


class RefreshDatabaseWorker(
    private val playlistDao: PlaylistDao,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDatabaseWorker"
    }

    override suspend fun doWork(): Result {
        return try {
//            playlistDao.getAllAudioInfo()
//                .mapParallel(Dispatchers.IO) { ytExtractor.extractVideoData(it.id)?.toAudioInfo() }
//                .filterNotNull().let { playlistDao.updatePlaylist(it) }
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }


    class Factory @Inject constructor(
        private val playlistDao: PlaylistDao
    ) : ChildWorkerFactory {

        override fun create(context: Context, params: WorkerParameters): ListenableWorker {
            return RefreshDatabaseWorker(playlistDao, context, params)
        }
    }
}