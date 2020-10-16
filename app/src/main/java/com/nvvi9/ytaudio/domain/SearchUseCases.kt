package com.nvvi9.ytaudio.domain

import com.nvvi9.ytaudio.data.datatype.Result
import com.nvvi9.ytaudio.repositories.SearchRepositoryImpl
import javax.inject.Inject


class SearchUseCase @Inject constructor(private val searchRepositoryImpl: SearchRepositoryImpl) {

    suspend fun getSuggestionList(query: String) =
        searchRepositoryImpl.getSuggestion(query).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.items?.mapNotNull { it.suggestion?.data })
                is Result.Error -> result
            }
        }
}