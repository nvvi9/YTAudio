package com.example.ytaudio.di.modules

import android.content.ComponentName
import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import com.example.ytaudio.db.AudioDatabase
import com.example.ytaudio.network.YTStreamApiService
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.repositories.YTVideoDetailsRemoteMediator
import com.example.ytaudio.service.AudioService
import com.example.ytaudio.service.AudioServiceConnection
import dagger.Module
import dagger.Provides
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
        database.playlistDao

    @Provides
    @Singleton
    fun provideVideoDetailsDao(database: AudioDatabase) =
        database.videoDetailsDao

    @Provides
    @Singleton
    fun provideVideoDetailsRemoteKeysDao(database: AudioDatabase) =
        database.videoDetailsRemoteKeysDao

    @Provides
    @Singleton
    fun provideMediaPlaybackServiceConnection(context: Context) =
        AudioServiceConnection.getInstance(
            context, ComponentName(context, AudioService::class.java)
        )

    @ExperimentalPagingApi
    @Provides
    @Singleton
    fun provideYTVideoDataRemoteMediator(
        db: AudioDatabase,
        ytApiService: YouTubeApiService,
        ytStreamApiService: YTStreamApiService
    ) =
        YTVideoDetailsRemoteMediator(
            ytApiService, ytStreamApiService, db,
            db.videoDetailsDao, db.videoDetailsRemoteKeysDao
        )
}