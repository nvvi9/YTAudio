package com.example.ytaudio.di.modules

import com.example.ytaudio.di.keys.WorkerKey
import com.example.ytaudio.workers.ChildWorkerFactory
import com.example.ytaudio.workers.RefreshDatabaseWorker
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