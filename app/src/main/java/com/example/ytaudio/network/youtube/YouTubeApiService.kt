package com.example.ytaudio.network.youtube

import com.example.ytaudio.BuildConfig
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


private const val YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_KEY


interface YouTubeApiService {

    @GET("search")
    fun getYTResponse(
        @Query("q") q: String,
        @Query("maxResults") maxResults: Int = 25,
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("key") key: String = YOUTUBE_API_KEY
    ): Deferred<YTResponse>
}