package com.nvvi9.ytaudio.repositories.paging

import androidx.paging.PagingSource
import com.nvvi9.YTStream
import com.nvvi9.ytaudio.data.datatype.Result
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.network.YouTubeNetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class YTVideoDetailsPagingSource @Inject constructor(
    private val ytNetworkDataSource: YouTubeNetworkDataSource,
    private val ytStream: YTStream
) : PagingSource<String, YTVideoDetails>() {

    override suspend fun load(params: LoadParams<String>) =
        ytNetworkDataSource.getPopular(params.loadSize, params.key).let { result ->
            when (result) {
                is Result.Success -> {
                    ytStream.extractVideoDetails(*result.data.items.map { it.id }.toTypedArray())
                        .toList()
                        .filterNotNull()
                        .map { YTVideoDetails.create(it) }
                        .let {
                            LoadResult.Page(
                                it, result.data.prevPageToken, result.data.nextPageToken
                            )
                        }
                }
                is Result.Error -> LoadResult.Error(result.throwable)
            }
        }
}