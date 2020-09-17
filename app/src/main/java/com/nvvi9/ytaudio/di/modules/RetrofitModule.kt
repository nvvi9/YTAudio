package com.nvvi9.ytaudio.di.modules

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.nvvi9.ytaudio.network.AutoCompleteService
import com.nvvi9.ytaudio.network.YouTubeApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton


@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
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
    fun provideAutoCompleteService(): AutoCompleteService =
        Retrofit.Builder()
            .baseUrl("https://suggestqueries.google.com/complete/")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(AutoCompleteService::class.java)
}