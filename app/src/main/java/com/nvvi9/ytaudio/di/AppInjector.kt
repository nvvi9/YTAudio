package com.nvvi9.ytaudio.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.work.Configuration
import androidx.work.WorkManager
import com.nvvi9.ytaudio.YTAudioApplication
import dagger.android.AndroidInjection
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection

object AppInjector {

    fun init(ytAudioApplication: YTAudioApplication) {
        DaggerAppComponent.builder()
            .application(ytAudioApplication)
            .context(ytAudioApplication)
            .build()
            .also {
                WorkManager.initialize(
                    ytAudioApplication,
                    Configuration.Builder()
                        .setWorkerFactory(it.workerFactory())
                        .build()
                )
            }.inject(ytAudioApplication)

        ytAudioApplication.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivity(activity)
            }

            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) = Unit
        })
    }

    private fun handleActivity(activity: Activity) {
        if (activity is HasAndroidInjector) {
            AndroidInjection.inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                .registerFragmentLifecycleCallbacks(
                    object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentCreated(
                            fm: FragmentManager,
                            f: Fragment,
                            savedInstanceState: Bundle?
                        ) {
                            if (f is Injectable) {
                                AndroidSupportInjection.inject(f)
                            }
                        }
                    }, true
                )
        }
    }
}