package com.nvvi9.ytaudio.ui.viewmodels

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.nvvi9.ytaudio.domain.YouTubeUseCases
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class YouTubeViewModel @Inject constructor(
    private val youTubeUseCases: YouTubeUseCases
) : YouTubeBaseViewModel() {

    init {
        updateYTItems()
    }

    override fun loadItems(query: String?): Flow<PagingData<YouTubeItem>?> =
        youTubeUseCases.getPopularYouTubeItems()
}