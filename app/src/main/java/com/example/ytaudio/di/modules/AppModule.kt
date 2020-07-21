package com.example.ytaudio.di.modules

import android.content.ComponentName
import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import com.example.ytaudio.db.AudioDatabase
import com.example.ytaudio.network.AutoCompleteService
import com.example.ytaudio.network.YTExtractor
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.repositories.YouTubeRemoteMediator
import com.example.ytaudio.service.AudioService
import com.example.ytaudio.service.AudioServiceConnection
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton


@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Singleton
    @Provides
    fun provideYTService(moshi: Moshi): YouTubeApiService =
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(YouTubeApiService::class.java)

    @Singleton
    @Provides
    fun provideYTExtractor() =
        YTExtractor()

    @Singleton
    @Provides
    fun provideAutoCompleteService(): AutoCompleteService =
        Retrofit.Builder()
            .baseUrl("https://suggestqueries.google.com/complete/")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(AutoCompleteService::class.java)

    @Singleton
    @Provides
    fun provideDb(context: Context) =
        Room.databaseBuilder(context, AudioDatabase::class.java, "YTAudio.db")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun providePlaylistDao(database: AudioDatabase) =
        database.playlistDao

    @Singleton
    @Provides
    fun provideYTRemoteKeysDao(database: AudioDatabase) =
        database.ytRemoteKeysDao

    @Singleton
    @Provides
    fun provideYTVideosItemDao(database: AudioDatabase) =
        database.ytVideosItemDao

    @Singleton
    @Provides
    fun provideMediaPlaybackServiceConnection(context: Context) =
        AudioServiceConnection.getInstance(
            context,
            ComponentName(context, AudioService::class.java)
        )

    @ExperimentalPagingApi
    @Singleton
    @Provides
    fun provideYouTubeRemoteMediator(db: AudioDatabase, apiService: YouTubeApiService) =
        YouTubeRemoteMediator(apiService, db, db.ytRemoteKeysDao, db.ytVideosItemDao)

    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.IO)

    @Provides
    fun provideDispatcherIO() = Dispatchers.IO
}