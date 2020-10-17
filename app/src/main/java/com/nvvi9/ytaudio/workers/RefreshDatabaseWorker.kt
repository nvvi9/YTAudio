package com.nvvi9.ytaudio.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.nvvi9.YTStream
import com.nvvi9.ytaudio.db.AudioInfoDao
import com.nvvi9.ytaudio.repositories.mapper.AudioInfoMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
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
            audioInfoDao.run {
                getAllAudioInfo().map { it.id }
                    .takeIf { it.isNotEmpty() }
                    ?.let { ytStream.extractVideoData(*it.toTypedArray()) }
                    ?.filterNotNull()
                    ?.mapNotNull { AudioInfoMapper.map(it) }
                    ?.toList()
                    ?.let { updatePlaylist(it) }
            }
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