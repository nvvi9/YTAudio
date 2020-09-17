package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.repositories.YTVideoDetailsRemoteMediator
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@Module
abstract class RemoteMediatorModule {

    @ExperimentalCoroutinesApi
    @FlowPreview
    @ExperimentalPagingApi
    @Binds
    @Singleton
    abstract fun bindYTVideoDataRemoteMediator(ytVideoDetailsRemoteMediator: YTVideoDetailsRemoteMediator): RemoteMediator<Int, YTVideoDetails>
}