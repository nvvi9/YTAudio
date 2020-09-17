package com.nvvi9.ytaudio.domain

import com.nvvi9.ytaudio.repositories.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchUseCases @Inject constructor(
    private val searchRepository: SearchRepository
) : UseCases {

    suspend fun getAutoCompleteList(query: String): List<String>? =
        searchRepository.getAutoComplete(query)?.items?.mapNotNull { it.suggestion?.data }
}