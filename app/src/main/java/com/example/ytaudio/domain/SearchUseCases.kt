package com.example.ytaudio.domain

import com.example.ytaudio.repositories.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

interface SearchUseCases {
    suspend fun getAutoCompleteList(query: String): List<String>?
}


@Singleton
class SearchUseCasesImpl @Inject constructor(
    private val searchRepository: SearchRepository
) : SearchUseCases {

    override suspend fun getAutoCompleteList(query: String): List<String>? =
        searchRepository.getAutoComplete(query)?.items?.mapNotNull { it.suggestion?.data }
}