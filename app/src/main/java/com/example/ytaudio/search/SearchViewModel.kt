package com.example.ytaudio.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ytaudio.domain.SearchItem
import com.example.ytaudio.repositories.SearchRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


class SearchViewModel @Inject constructor(private val repository: SearchRepository) : ViewModel() {

    val searchItemList = repository.searchItemList
    val autoComplete = repository.autoCompleteList

    fun setResponse(query: String, maxResults: Int = 25) {
        viewModelScope.launch {
            try {
                this@SearchViewModel.repository.setItemsFromResponse(query, maxResults)
            } catch (t: Throwable) {
                Log.i("SearchViewModel", t.message ?: "error")
            }
        }
    }

    fun setAutoComplete(query: String) {
        viewModelScope.launch {
            try {
                repository.setAutocomplete(query)
            } catch (t: Throwable) {
                Log.i("SearchViewModel", t.message ?: "error")
            }
        }
    }

    fun insertInDatabase(items: List<SearchItem>) {
        viewModelScope.launch {
            repository.addDatabase(items)
        }
    }
}