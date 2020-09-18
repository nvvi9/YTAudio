package com.nvvi9.ytstream.model.raw

import com.nvvi9.ytstream.model.VideoDetails
import com.nvvi9.ytstream.network.KtorService
import com.nvvi9.ytstream.utils.ifNotNull
import com.nvvi9.ytstream.utils.tryOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow


internal class Raw(val videoPageSource: String, val videoDetails: VideoDetails) {

    companion object {

        suspend fun fromIdFlow(id: String) = flow {
            emit(fromId(id))
        }

        private suspend fun fromId(id: String) = coroutineScope {
            val videoPageSource = async {
                tryOrNull {
                    KtorService.getVideoPage(id).replace("\\\"", "\"")
                }
            }

            val videoDetails = async {
                VideoDetails.fromId(id)
            }

            ifNotNull(videoPageSource.await(), videoDetails.await()) { pageSource, details ->
                Raw(pageSource, details)
            }
        }
    }
}