package com.nvvi9.ytaudio.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.nvvi9.ytaudio.repositories.AudioInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class RefreshDatabaseWorker(
    private val audioInfoRepository: AudioInfoRepository,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDatabaseWorker"
    }

    override suspend fun doWork() =
        try {
            audioInfoRepository.updateAll()
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }


    class Factory @Inject constructor(
        private val audioInfoRepository: AudioInfoRepository
    ) : ChildWorkerFactory {

        override fun create(context: Context, params: WorkerParameters): ListenableWorker {
            return RefreshDatabaseWorker(audioInfoRepository, context, params)
        }
    }
}