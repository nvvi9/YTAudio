package com.nvvi9.ytaudio.repositories.paging

import androidx.paging.PagingSource
import com.nvvi9.YTStream
import com.nvvi9.ytaudio.data.datatype.Result
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.network.YouTubeNetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList


@ExperimentalCoroutinesApi
@FlowPreview
class YTSearchPagingSource(
    private val query: String,
    private val ytNetworkDataSource: YouTubeNetworkDataSource,
    private val ytStream: YTStream,
) : PagingSource<String, YTVideoDetails>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, YTVideoDetails> =
        ytNetworkDataSource.getFromQuery(query, params.loadSize, params.key).run {
            when (this) {
                is Result.Success -> {
                    ytStream.extractVideoDetails(*data.items.map { it.id.videoId }.toTypedArray())
                        .toList()
                        .filterNotNull()
                        .map { YTVideoDetails.create(it) }
                        .let {
                            LoadResult.Page(
                                it, data.prevPageToken, data.nextPageToken
                            )
                        }
                }
                is Result.Error -> LoadResult.Error(throwable)
            }
        }
}