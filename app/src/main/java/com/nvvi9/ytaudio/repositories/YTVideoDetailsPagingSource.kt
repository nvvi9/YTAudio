package com.nvvi9.ytaudio.repositories

import android.util.Log
import androidx.paging.PagingSource
import com.nvvi9.YTStream
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.network.YouTubeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class YTVideoDetailsPagingSource @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytStream: YTStream
) : PagingSource<String, YTVideoDetails>() {

    override suspend fun load(params: LoadParams<String>) =
        try {
            val startTime = System.currentTimeMillis()
            ytApiService.getYTVideosIdResponse(params.loadSize, params.key).run {
                ytStream.extractVideoDetails(*items.map { it.id }.toTypedArray())
                    .toList().filterNotNull()
                    .map { YTVideoDetails.create(it) }
                    .let { LoadResult.Page(it, prevPageToken, nextPageToken) }
            }.also {
                Log.i("VideoDetailsPaging", "${System.currentTimeMillis() - startTime}")
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
}