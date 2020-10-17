package com.nvvi9.ytaudio.network

import com.nvvi9.YTStream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.single
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class YTStreamDataSource @Inject constructor(private val ytStream: YTStream) {

    suspend fun extractVideoData(id: String) =
        ytStream.extractVideoData(id).single()

    suspend fun extractVideoDetails(id: List<String>) =
        ytStream.extractVideoDetails(*id.toTypedArray())
}