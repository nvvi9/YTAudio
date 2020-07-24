package com.example.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.example.ytaudio.data.videodata.VideoData
import com.example.ytaudio.repositories.YTVideoDataRemoteMediator
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class RemoteMediatorModule {

    @ExperimentalPagingApi
    @Binds
    @Singleton
    abstract fun bindYTVideoDataRemoteMediator(ytVideoDataRemoteMediator: YTVideoDataRemoteMediator): RemoteMediator<Int, VideoData>
}