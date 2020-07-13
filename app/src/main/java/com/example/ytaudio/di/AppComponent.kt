package com.example.ytaudio.di

import android.app.Application
import android.content.Context
import com.example.ytaudio.YTAudioApplication
import com.example.ytaudio.di.factories.YTAudioWorkerFactory
import com.example.ytaudio.di.modules.AppModule
import com.example.ytaudio.di.modules.MainActivityModule
import com.example.ytaudio.di.modules.MediaPlaybackServiceModule
import com.example.ytaudio.di.modules.WorkerModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        MainActivityModule::class,
        MediaPlaybackServiceModule::class,
        WorkerModule::class
    ]
)
interface AppComponent : AndroidInjector<YTAudioApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    override fun inject(ytAudioApplication: YTAudioApplication)
    fun factory(): YTAudioWorkerFactory
}