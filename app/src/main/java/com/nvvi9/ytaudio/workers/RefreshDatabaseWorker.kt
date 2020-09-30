package com.nvvi9.ytaudio.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.nvvi9.YTStream
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo
import com.nvvi9.ytaudio.db.AudioInfoDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class RefreshDatabaseWorker(
    private val ytStream: YTStream,
    private val audioInfoDao: AudioInfoDao,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDatabaseWorker"
    }

    override suspend fun doWork() =
        try {
            audioInfoDao.getAllAudioInfo()
                .takeIf { it.isNotEmpty() }
                ?.map { it.id }
                ?.let { ytStream.extractVideoData(*it.toTypedArray()).toList().filterNotNull() }
                ?.mapNotNull { AudioInfo.fromVideoData(it) }
                ?.let { audioInfoDao.updatePlaylist(it) }
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }


    class Factory @Inject constructor(
        private val ytStream: YTStream,
        private val audioInfoDao: AudioInfoDao
    ) : ChildWorkerFactory {

        override fun create(context: Context, params: WorkerParameters): ListenableWorker =
            RefreshDatabaseWorker(ytStream, audioInfoDao, context, params)
    }
}