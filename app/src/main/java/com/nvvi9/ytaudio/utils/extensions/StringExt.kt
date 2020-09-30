package com.nvvi9.ytaudio.utils.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL


suspend fun String.urlToByteArray() =
    withContext(Dispatchers.IO) {
        URL(this@urlToByteArray).openStream().readBytes()
    }