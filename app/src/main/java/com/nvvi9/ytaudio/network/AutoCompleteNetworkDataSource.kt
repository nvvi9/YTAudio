package com.nvvi9.ytaudio.network

import com.nvvi9.ytaudio.data.datatype.Result
import com.nvvi9.ytaudio.network.retrofit.AutoCompleteService
import javax.inject.Inject


class AutoCompleteNetworkDataSource @Inject constructor(private val autoCompleteService: AutoCompleteService) {

    suspend fun getFromQuery(query: String) =
        try {
            autoCompleteService.getAutoComplete(query).let {
                Result.Success(it)
            }
        } catch (t: Throwable) {
            Result.Error(t)
        }
}