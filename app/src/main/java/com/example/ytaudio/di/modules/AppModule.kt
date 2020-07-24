package com.example.ytaudio.di.modules

import android.content.ComponentName
import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import com.example.ytaudio.db.AudioDatabase
import com.example.ytaudio.network.AutoCompleteService
import com.example.ytaudio.network.YTExtractor
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.repositories.YTVideoDataRemoteMediator
import com.example.ytaudio.repositories.YouTubeVideosRemoteMediator
import com.example.ytaudio.service.AudioService
import com.example.ytaudio.service.AudioServiceConnection
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton


@Module(includes = [ViewModelModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideYTService(moshi: Moshi): YouTubeApiService =
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(YouTubeApiService::class.java)

    @Provides
    @Singleton
    fun provideYTExtractor() =
        YTExtractor()

    @Provides
    @Singleton
    fun provideAutoCompleteService(): AutoCompleteService =
        Retrofit.Builder()
            .baseUrl("https://suggestqueries.google.com/complete/")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(AutoCompleteService::class.java)

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
    fun provideYTVideosItemDao(database: AudioDatabase) =
        database.ytVideosItemDao

    @Provides
    @Singleton
    fun provideVideoDataDao(database: AudioDatabase) =
        database.videoDataDao

    @Provides
    @Singleton
    fun provideVideoDataRemoteKeys(database: AudioDatabase) =
        database.videoDataRemoteKeysDao

    @Provides
    @Singleton
    fun provideYTRemoteKeysDao(database: AudioDatabase) =
        database.ytVideosRemoteKeysDao

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
        apiService: YouTubeApiService,
        ytExtractor: YTExtractor
    ) =
        YTVideoDataRemoteMediator(
            apiService, ytExtractor, db,
            db.videoDataRemoteKeysDao,
            db.videoDataDao
        )

    @ExperimentalPagingApi
    @Provides
    @Singleton
    fun provideYouTubeRemoteMediator(db: AudioDatabase, apiService: YouTubeApiService) =
        YouTubeVideosRemoteMediator(apiService, db, db.ytVideosRemoteKeysDao, db.ytVideosItemDao)
}