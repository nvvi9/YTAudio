package com.nvvi9.ytaudio.di.modules

import com.nvvi9.ytaudio.di.keys.WorkerKey
import com.nvvi9.ytaudio.workers.ChildWorkerFactory
import com.nvvi9.ytaudio.workers.RefreshDatabaseWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(RefreshDatabaseWorker::class)
    abstract fun bindRefreshDatabaseWorkerFactory(factory: RefreshDatabaseWorker.Factory): ChildWorkerFactory
}