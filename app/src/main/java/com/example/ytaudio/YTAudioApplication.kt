package com.example.ytaudio

import android.app.Application
import androidx.work.*
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

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

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