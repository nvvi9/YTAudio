package com.example.ytaudio.network

import com.example.ytaudio.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val AUTOCOMPLETE_URL =
    "https://suggestqueries.google.com/complete/"

private const val BASE_URL =
    "https://www.googleapis.com/youtube/v3/"

private const val API_KEY = BuildConfig.YOUTUBE_API_KEY

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

private val retrofitComplete = Retrofit.Builder()
    .addConverterFactory(SimpleXmlConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(AUTOCOMPLETE_URL)
    .build()

interface YouTubeApiService {
    @GET("search")
    fun getSearchResponse(
        @Query("q") q: String,
        @Query("maxResults") maxResults: Int = 25,
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("key") key: String = API_KEY
    ): Deferred<Response>

    @GET("search")
    fun getAutoComplete(
        @Query("q") q: String,
        @Query("client") client: String = "toolbar",
        @Query("ds") ds: String = "yt"
    ): Deferred<AutoComplete>
}


object YouTubeApi {
    val retrofitService: YouTubeApiService by lazy {
        retrofit.create(YouTubeApiService::class.java)
    }

    val retrofitAutoComplete: YouTubeApiService by lazy {
        retrofitComplete.create(YouTubeApiService::class.java)
    }
}