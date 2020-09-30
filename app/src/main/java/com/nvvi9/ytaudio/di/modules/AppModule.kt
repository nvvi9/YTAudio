package com.nvvi9.ytaudio.di.modules

import android.content.ComponentName
import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import com.nvvi9.YTStream
import com.nvvi9.ytaudio.db.AudioDatabase
import com.nvvi9.ytaudio.network.YouTubeApiService
import com.nvvi9.ytaudio.repositories.YTVideoDetailsRemoteMediator
import com.nvvi9.ytaudio.service.AudioService
import com.nvvi9.ytaudio.service.AudioServiceConnection
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@FlowPreview
@Module(includes = [ViewModelModule::class, RetrofitModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideDb(context: Context) =
        Room.databaseBuilder(context, AudioDatabase::class.java, "YTAudio.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providePlaylistDao(database: AudioDatabase) =
        database.audioInfoDao

    @Provides
    @Singleton
    fun provideVideoDetailsDao(database: AudioDatabase) =
        database.ytVideoDetailsDao

    @Provides
    @Singleton
    fun provideVideoDetailsRemoteKeysDao(database: AudioDatabase) =
        database.ytVideoDetailsRemoteKeysDao

    @Provides
    @Singleton
    fun provideMediaPlaybackServiceConnection(context: Context) =
        AudioServiceConnection.getInstance(
            context, ComponentName(context, AudioService::class.java)
        )


    @Provides
    fun provideYTStream() = YTStream()

    @Provides
    @Singleton
    fun provideYTVideoDataRemoteMediator(
        db: AudioDatabase,
        ytApiService: YouTubeApiService,
        ytStream: YTStream
    ) =
        YTVideoDetailsRemoteMediator(
            ytApiService, ytStream, db,
            db.ytVideoDetailsDao, db.ytVideoDetailsRemoteKeysDao
        )
}