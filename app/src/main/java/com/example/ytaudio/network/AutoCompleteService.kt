package com.example.ytaudio.network

import com.example.ytaudio.data.autocomplete.AutoComplete
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


interface AutoCompleteService {

    @GET("search")
    fun getAutoCompleteAsync(
        @Query("q") q: String,
        @Query("ds") ds: String = "yt",
        @Query("client") client: String = "toolbar"
    ): Deferred<AutoComplete>
}