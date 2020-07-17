package com.example.ytaudio.di.modules

import android.content.ComponentName
import android.content.Context
import androidx.room.Room
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.network.autocomplete.AutoCompleteService
import com.example.ytaudio.network.extractor.YTExtractor
import com.example.ytaudio.network.youtube.YouTubeApiService
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
    fun provideMoshi() =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Singleton
    @Provides
    fun provideYTService(moshi: Moshi) =
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
    fun provideAutoCompleteService() =
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
    fun provideDao(audioDatabase: AudioDatabase) =
        audioDatabase.audioDatabaseDao

    @Singleton
    @Provides
    fun provideMediaPlaybackServiceConnection(context: Context) =
        AudioServiceConnection.getInstance(
            context,
            ComponentName(context, AudioService::class.java)
        )

    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.IO)
}