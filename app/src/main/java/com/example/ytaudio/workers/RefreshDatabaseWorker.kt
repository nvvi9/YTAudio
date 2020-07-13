package com.example.ytaudio.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.ytaudio.repositories.AudioRepository
import javax.inject.Inject


class RefreshDatabaseWorker(
    private val audioRepository: AudioRepository,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDatabaseWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            audioRepository.updateAllAudioInfo()
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }


    class Factory @Inject constructor(
        private val audioRepository: AudioRepository
    ) : ChildWorkerFactory {
        override fun create(context: Context, params: WorkerParameters): ListenableWorker {
            return RefreshDatabaseWorker(audioRepository, context, params)
        }
    }
}