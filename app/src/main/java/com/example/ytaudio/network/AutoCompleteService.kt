package com.example.ytaudio.network

import com.example.ytaudio.data.autocomplete.AutoComplete
import retrofit2.http.GET
import retrofit2.http.Query


interface AutoCompleteService {

    @GET("search?client=toolbar&ds=yt")
    suspend fun getAutoComplete(@Query("q") q: String): AutoComplete
}