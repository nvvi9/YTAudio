package com.example.ytaudio.network

import com.example.ytaudio.network.autocomplete.AutoCompleteService
import com.example.ytaudio.network.youtube.YouTubeApiService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

private const val YOUTUBE_BASE_URL =
    "https://www.googleapis.com/youtube/v3/"

private const val AUTOCOMPLETE_BASE_URL =
    "https://suggestqueries.google.com/complete/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofitYTResponse = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(YOUTUBE_BASE_URL)
    .build()

private val retrofitAutoComplete = Retrofit.Builder()
    .addConverterFactory(SimpleXmlConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(AUTOCOMPLETE_BASE_URL)
    .build()


object ApiService {

    val ytService: YouTubeApiService by lazy {
        retrofitYTResponse.create(YouTubeApiService::class.java)
    }

    val autoCompleteService: AutoCompleteService by lazy {
        retrofitAutoComplete.create(AutoCompleteService::class.java)
    }
}