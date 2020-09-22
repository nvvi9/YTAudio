package com.nvvi9.ytaudio.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo
import com.nvvi9.ytaudio.db.AudioInfoDao
import com.nvvi9.ytaudio.utils.Constants.KEY_YT_ID_WORKER
import com.nvvi9.ytstream.YTStream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class YTStreamWorker(
    private val ytStream: YTStream,
    private val audioInfoDao: AudioInfoDao,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "YTStreamWorker"
    }

    override suspend fun doWork(): Result =
        try {
            Log.i("YTStreamWorker", "here")
            inputData.getString(KEY_YT_ID_WORKER).also {
                Log.i("YTStreamWorker", "id: $it")
            }?.let { id ->
                Log.i("YTStreamWorker", id)
                ytStream.extractVideoData(id)
                    .toList().filterNotNull()
                    .mapNotNull { AudioInfo.fromVideoData(it) }
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        audioInfoDao.insert(it)
                        Result.success()
                    } ?: Result.failure()
            } ?: Result.failure()
        } catch (t: Throwable) {
            Result.retry()
        }


    class Factory @Inject constructor(
        private val ytStream: YTStream,
        private val audioInfoDao: AudioInfoDao
    ) : ChildWorkerFactory {

        override fun create(context: Context, params: WorkerParameters): ListenableWorker =
            YTStreamWorker(ytStream, audioInfoDao, context, params)
    }
}