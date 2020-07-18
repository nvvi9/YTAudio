package com.example.ytaudio.repositories

import com.example.ytaudio.data.youtube.YTSearchResponse
import com.example.ytaudio.data.youtube.YTVideosResponse
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.repositories.base.YouTubeRepository
import com.example.ytaudio.vo.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class YouTubeRepositoryImpl @Inject constructor(
    private val ytApiService: YouTubeApiService
) : YouTubeRepository {

    override suspend fun getVideosResponse(): Result<YTVideosResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = ytApiService.getYTVideosResponseAsync().await()
                Result.Success(response)
            } catch (t: Throwable) {
                Result.Error<YTVideosResponse>(t)
            }
        }

    override suspend fun getSearchResponse(query: String): Result<YTSearchResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = ytApiService.getYTSearchResponseAsync(query).await()
                Result.Success(response)
            } catch (t: Throwable) {
                Result.Error<YTSearchResponse>(t)
            }
        }
}