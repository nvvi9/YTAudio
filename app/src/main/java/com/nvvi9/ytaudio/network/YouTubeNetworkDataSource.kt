package com.nvvi9.ytaudio.network

import com.nvvi9.ytaudio.data.datatype.Result
import com.nvvi9.ytaudio.network.retrofit.YouTubeApiService
import kotlinx.coroutines.*
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class YouTubeNetworkDataSource @Inject constructor(private val youTubeApiService: YouTubeApiService) {

    suspend fun getFromQuery(query: String, maxResults: Int, pageToken: String?) =
        try {
            youTubeApiService.getYTSearchPartId(query, maxResults, pageToken).let {
                Result.Success(it)
            }
        } catch (t: Throwable) {
            Result.Error(t)
        }

    suspend fun getPopular(maxResults: Int, pageToken: String?) =
        try {
            youTubeApiService.getYTVideosIdResponse(maxResults, pageToken).let {
                Result.Success(it)
            }
        } catch (t: Throwable) {
            Result.Error(t)
        }

    suspend fun getPlaylistItems(playlistId: List<String>) =
        coroutineScope {
            try {
                playlistId.map {
                    async { it to youTubeApiService.getYTPlaylistItems(it) }
                }.awaitAll().toMap().let { Result.Success(it) }
            } catch (t: Throwable) {
                Result.Error(t)
            }
        }

    suspend fun getPlaylistItems(playlistId: String) =
        try {
            youTubeApiService.getYTPlaylistItems(playlistId).let {
                Result.Success(it)
            }
        } catch (t: Throwable) {
            Result.Error(t)
        }
}