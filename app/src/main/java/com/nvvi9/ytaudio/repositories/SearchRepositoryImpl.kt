package com.nvvi9.ytaudio.repositories

import com.nvvi9.ytaudio.network.AutoCompleteNetworkDataSource
import com.nvvi9.ytaudio.repositories.base.SearchRepository
import javax.inject.Inject


class SearchRepositoryImpl @Inject constructor(
    private val autoCompleteNetworkDataSource: AutoCompleteNetworkDataSource
) : SearchRepository {

    override suspend fun getSuggestion(query: String) =
        autoCompleteNetworkDataSource.getFromQuery(query)
}