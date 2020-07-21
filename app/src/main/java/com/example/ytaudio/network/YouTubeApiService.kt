package com.example.ytaudio.network

import com.example.ytaudio.BuildConfig
import com.example.ytaudio.data.youtube.YTSearchResponse
import com.example.ytaudio.data.youtube.YTVideosResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("search?key=${BuildConfig.YOUTUBE_API_KEY}&part=snippet&type=video")
    suspend fun getYTSearchResponse(
        @Query("q") q: String,
        @Query("maxResults") maxResults: Int = 25
    ): YTSearchResponse

    @GET("videos?key=${BuildConfig.YOUTUBE_API_KEY}&part=snippet&chart=mostPopular&videoCategoryId=10")
    suspend fun getYTVideosResponse(
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") pageToken: String? = null
    ): YTVideosResponse
}