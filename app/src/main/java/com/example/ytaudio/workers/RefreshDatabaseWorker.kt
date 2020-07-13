package com.example.ytaudio.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ytaudio.YTAudioApplication
import com.example.ytaudio.di.DaggerAppComponent
import com.example.ytaudio.repositories.AudioRepository
import javax.inject.Inject


class RefreshDatabaseWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    @Inject
    lateinit var audioRepository: AudioRepository

    init {
        DaggerAppComponent.builder()
            .application(context as YTAudioApplication)
            .context(context)
            .build()
            .inject(this)
    }

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
}