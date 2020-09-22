package com.nvvi9.ytaudio.di.modules

import com.nvvi9.ytaudio.di.keys.WorkerKey
import com.nvvi9.ytaudio.workers.ChildWorkerFactory
import com.nvvi9.ytaudio.workers.RefreshDatabaseWorker
import com.nvvi9.ytaudio.workers.YTStreamWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@Module
@FlowPreview
@ExperimentalCoroutinesApi
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(RefreshDatabaseWorker::class)
    abstract fun bindRefreshDatabaseWorkerFactory(factory: RefreshDatabaseWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(YTStreamWorker::class)
    abstract fun bindYTStreamWorker(factory: RefreshDatabaseWorker.Factory): ChildWorkerFactory
}