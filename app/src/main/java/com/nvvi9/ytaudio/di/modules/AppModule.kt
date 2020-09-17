package com.nvvi9.ytaudio.di.modules

import android.content.ComponentName
import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import com.nvvi9.ytaudio.db.AudioDatabase
import com.nvvi9.ytaudio.network.YouTubeApiService
import com.nvvi9.ytaudio.repositories.YTVideoDetailsRemoteMediator
import com.nvvi9.ytaudio.service.AudioService
import com.nvvi9.ytaudio.service.AudioServiceConnection
import com.nvvi9.ytstream.YTStream
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


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

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Provides
    fun provideYTStream() = YTStream()

    @ExperimentalCoroutinesApi
    @FlowPreview
    @ExperimentalPagingApi
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