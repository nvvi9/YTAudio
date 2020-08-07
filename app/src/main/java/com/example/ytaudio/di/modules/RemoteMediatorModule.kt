package com.example.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.example.ytaudio.data.streamyt.VideoDetails
import com.example.ytaudio.repositories.YTVideoDetailsRemoteMediator
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class RemoteMediatorModule {

    @ExperimentalPagingApi
    @Binds
    @Singleton
    abstract fun bindYTVideoDataRemoteMediator(ytVideoDetailsRemoteMediator: YTVideoDetailsRemoteMediator): RemoteMediator<Int, VideoDetails>
}