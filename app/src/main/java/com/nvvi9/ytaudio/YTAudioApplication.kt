package com.nvvi9.ytaudio

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import androidx.work.*
import com.nvvi9.ytaudio.di.AppInjector
import com.nvvi9.ytaudio.workers.RefreshDatabaseWorker
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class YTAudioApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @ExperimentalPagingApi
    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        delayedInit()
    }

    override fun androidInjector() = androidInjector

    private fun delayedInit() =
        coroutineScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val periodicWorkRequest =
                PeriodicWorkRequestBuilder<RefreshDatabaseWorker>(1, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(this@YTAudioApplication)
                .enqueueUniquePeriodicWork(
                    RefreshDatabaseWorker.WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWorkRequest
                )
        }
}