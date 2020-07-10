package com.example.ytaudio.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ytaudio.domain.SearchItem
import com.example.ytaudio.repositories.SearchRepository
import kotlinx.coroutines.launch


class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SearchRepository(application)

    val searchItemList = repository.searchItemList
    val autoComplete = repository.autoCompleteList


    fun setResponse(query: String, maxResults: Int = 25) {
        viewModelScope.launch {
            try {
                repository.setItemsFromResponse(query, maxResults)
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


    class Factory(private val application: Application) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(application) as T
        }
    }
}