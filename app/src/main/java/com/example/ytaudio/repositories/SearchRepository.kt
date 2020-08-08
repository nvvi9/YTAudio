package com.example.ytaudio.repositories

import com.example.ytaudio.data.autocomplete.AutoComplete
import com.example.ytaudio.network.AutoCompleteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val autoCompleteService: AutoCompleteService
) : Repository {

    suspend fun getAutoComplete(query: String): AutoComplete? =
        withContext(Dispatchers.IO) {
            try {
                autoCompleteService.getAutoComplete(query)
            } catch (t: Throwable) {
                null
            }
        }
}