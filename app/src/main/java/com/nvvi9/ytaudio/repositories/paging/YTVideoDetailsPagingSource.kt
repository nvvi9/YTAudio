package com.nvvi9.ytaudio.repositories.paging

import androidx.paging.PagingSource
import com.nvvi9.ytaudio.data.datatype.Result
import com.nvvi9.ytaudio.data.ytstream.YTVideoItems
import com.nvvi9.ytaudio.network.YTStreamDataSource
import com.nvvi9.ytaudio.network.YouTubeNetworkDataSource
import com.nvvi9.ytaudio.repositories.mapper.YTVideoMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class YTVideoDetailsPagingSource @Inject constructor(
    private val ytNetworkDataSource: YouTubeNetworkDataSource,
    private val ytStreamDataSource: YTStreamDataSource
) : PagingSource<String, YTVideoItems>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, YTVideoItems> =
        ytNetworkDataSource.getPopular(params.loadSize, params.key).run {
            when (this) {
                is Result.Success -> {
                    ytStreamDataSource.extractVideoDetails(data.items.map { it.id })
                        .filterNotNull()
                        .map { YTVideoMapper.map(it) }
                        .toList()
                        .let { LoadResult.Page(it, data.prevPageToken, data.nextPageToken) }
                }
                is Result.Error -> LoadResult.Error(throwable)
            }
        }
}