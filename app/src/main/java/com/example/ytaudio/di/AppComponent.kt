package com.example.ytaudio.di

import android.app.Application
import android.content.Context
import com.example.ytaudio.YTAudioApplication
import com.example.ytaudio.workers.RefreshDatabaseWorker
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, MainActivityModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(ytAudioApplication: YTAudioApplication)
    fun inject(worker: RefreshDatabaseWorker)
}