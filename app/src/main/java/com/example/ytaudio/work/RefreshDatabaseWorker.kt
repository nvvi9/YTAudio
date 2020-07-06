package com.example.ytaudio.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ytaudio.repositories.AudioRepository


class RefreshDatabaseWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDatabaseWorker"
    }

    override suspend fun doWork(): Result {
        val repository = AudioRepository(applicationContext)

        return try {
            repository.updateAllAudioInfo()
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }
}