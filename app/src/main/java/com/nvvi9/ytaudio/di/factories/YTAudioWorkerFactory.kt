package com.nvvi9.ytaudio.di.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.nvvi9.ytaudio.workers.ChildWorkerFactory
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton


@Singleton
class YTAudioWorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = workerFactories.entries.find {
        Class.forName(workerClassName).isAssignableFrom(it.key)
    }?.value?.get()?.create(appContext, workerParameters)
}