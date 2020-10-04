package com.nvvi9.ytaudio.repositories

import com.nvvi9.ytaudio.network.AutoCompleteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SearchRepository @Inject constructor(
    private val autoCompleteService: AutoCompleteService
) {

    suspend fun getAutoComplete(query: String) =
        withContext(Dispatchers.IO) {
            autoCompleteService.getAutoComplete(query).items?.mapNotNull { it.suggestion?.data }
        }
}