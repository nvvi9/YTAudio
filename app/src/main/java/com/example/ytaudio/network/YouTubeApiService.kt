package com.example.ytaudio.network

import com.example.ytaudio.BuildConfig
import com.example.ytaudio.data.youtube.YTSearchResponse
import com.example.ytaudio.data.youtube.YTVideosResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("search")
    fun getYTSearchResponseAsync(
        @Query("q") q: String,
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 25,
        @Query("key") key: String = BuildConfig.YOUTUBE_API_KEY
    ): Deferred<YTSearchResponse>

    @GET("videos")
    fun getYTVideosResponseAsync(
        @Query("chart") chart: String = "mostPopular",
        @Query("videoCategoryId") categoryId: String = "10",
        @Query("part") part: String = "snippet",
        @Query("maxResults") maxResults: Int = 25,
        @Query("key") key: String = BuildConfig.YOUTUBE_API_KEY
    ): Deferred<YTVideosResponse>
}