package com.nvvi9.ytaudio

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nvvi9.YTStream
import com.nvvi9.model.streams.StreamType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith


@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ExtractionTest {

    private val ytStream = YTStream()

    @Test
    fun byteArrayTest() = runBlocking<Unit> {
        ytStream.extractVideoData("9PaTQgIOy70").single()?.let { videoData ->
            videoData.streams.filter {
                it.streamDetails.type == StreamType.AUDIO
            }.random().url.let {
//                URL(it).readBytes()
            }
        }
    }
}