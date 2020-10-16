package com.nvvi9.ytaudio.repositories.base

import com.nvvi9.ytaudio.data.autocomplete.AutoComplete
import com.nvvi9.ytaudio.data.datatype.Result


interface SearchRepository {
    suspend fun getSuggestion(query: String): Result<AutoComplete>
}