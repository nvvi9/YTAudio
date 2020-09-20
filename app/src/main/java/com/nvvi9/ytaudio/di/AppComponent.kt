package com.nvvi9.ytaudio.di

import android.app.Application
import android.content.Context
import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.YTAudioApplication
import com.nvvi9.ytaudio.di.factories.YTAudioWorkerFactory
import com.nvvi9.ytaudio.di.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        MainActivityModule::class,
        AudioServiceModule::class,
        WorkerModule::class,
        RepositoryModule::class,
        UseCasesModule::class,
        PagingModule::class
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

    fun workerFactory(): YTAudioWorkerFactory
}