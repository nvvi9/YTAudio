package com.nvvi9.ytaudio.repositories

import androidx.paging.PagingSource
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.network.YouTubeApiService
import com.nvvi9.ytstream.YTStream
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


    override suspend fun load(params: LoadParams<String>): LoadResult<String, YTVideoDetails> =
        try {
            ytApiService.getYTVideosIdResponse(params.loadSize, params.key).let { ytVideosPartId ->
                ytStream.extractVideoDetails(*ytVideosPartId.items.map { it.id }.toTypedArray())
                    .toList().filterNotNull()
                    .map { YTVideoDetails.create(it) }
                    .let {
                        LoadResult.Page(
                            it, ytVideosPartId.prevPageToken, ytVideosPartId.nextPageToken
                        )
                    }
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
}