package com.nvvi9.ytaudio.network

import com.nvvi9.ytaudio.data.autocomplete.AutoComplete
import retrofit2.http.GET
import retrofit2.http.Query


interface AutoCompleteService {

    @GET("search?client=toolbar&ds=yt")
    suspend fun getAutoComplete(@Query("q") q: String): AutoComplete
}