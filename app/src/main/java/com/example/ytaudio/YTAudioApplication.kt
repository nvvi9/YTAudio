package com.example.ytaudio

import android.app.Application
import androidx.work.*
import com.example.ytaudio.di.AppComponent
import com.example.ytaudio.di.AppInjector
import com.example.ytaudio.workers.RefreshDatabaseWorker
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class YTAudioApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private lateinit var appComponent: AppComponent

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        appComponent = AppInjector.init(this)

        val workerFactory = appComponent.factory()

        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )
        delayedInit()
    }

    override fun androidInjector() = androidInjector

    private fun delayedInit() =
        coroutineScope.launch {
            setupWork()
        }

    private fun setupWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<RefreshDatabaseWorker>(2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                RefreshDatabaseWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
    }
}