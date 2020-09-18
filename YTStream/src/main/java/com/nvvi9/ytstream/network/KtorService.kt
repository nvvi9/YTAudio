package com.nvvi9.ytstream.network

import com.nvvi9.ytstream.utils.encode
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient


internal object KtorService {

    private val ktor = HttpClient(OkHttp) {
        engine {
            preconfigured = OkHttpClient.Builder()
                .addNetworkInterceptor {
                    it.proceed(
                        it.request()
                            .newBuilder()
                            .header(
                                "User-Agent",
                                "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0"
                            )
                            .build()
                    )
                }
                .build()
        }
        expectSuccess = true
    }

    suspend fun getVideoPage(id: String) = withContext(Dispatchers.IO) {
        ktor.get<String>("https://www.youtube.com/watch?v=$id")
    }

    suspend fun getVideoInfo(id: String) = withContext(Dispatchers.IO) {
        ktor.get<String>("https://www.youtube.com/get_video_info?video_id=$id&eurl=${"https://youtube.googleapis.com/v/$id".encode()}")
    }

    suspend fun getJsFile(jsPath: String) = withContext(Dispatchers.IO) {
        ktor.get<String>("https://www.youtube.com/s/$jsPath")
    }
}