package com.nvvi9.ytaudio.network.retrofit

import com.nvvi9.ytaudio.BuildConfig
import com.nvvi9.ytaudio.data.youtube.YTPlaylistItems
import com.nvvi9.ytaudio.data.youtube.YTSearchPartId
import com.nvvi9.ytaudio.data.youtube.YTVideosPartId
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("search?key=${BuildConfig.YOUTUBE_API_KEY}&part=id&type=video,playlist")
    suspend fun getYTSearchPartId(
        @Query("q") q: String,
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") pageToken: String?
    ): YTSearchPartId

    @GET("videos?key=${BuildConfig.YOUTUBE_API_KEY}&part=id&chart=mostPopular&videoCategoryId=0")
    suspend fun getYTVideosIdResponse(
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") pageToken: String? = null
    ): YTVideosPartId

    @GET("playlistItems?key=${BuildConfig.YOUTUBE_API_KEY}&part=contentDetails")
    suspend fun getYTPlaylistItems(
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int = 50,
        @Query("pageToken") pageToken: String? = null
    ): YTPlaylistItems
}