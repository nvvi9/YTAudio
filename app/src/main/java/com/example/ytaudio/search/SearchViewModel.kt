package com.example.ytaudio.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.domain.SearchItem
import com.example.ytaudio.repositories.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private val repository = SearchRepository(application)

    val searchItemList = repository.searchItemList
    val autoComplete = repository.autoCompleteList

    fun setResponse(query: String, maxResults: Int = 25) {
        coroutineScope.launch {
            try {
                repository.setItemsFromResponse(query, maxResults)
            } catch (t: Throwable) {
                Log.i("SearchViewModel", t.message ?: "error")
            }
        }
    }

    fun setAutoComplete(query: String) {
        coroutineScope.launch {
            try {
                repository.setAutocomplete(query)
            } catch (t: Throwable) {
                Log.i("SearchViewModel", t.message ?: "error")
            }
        }
    }

    fun insertInDatabase(items: List<SearchItem>) {
        coroutineScope.launch {
            repository.addToDatabase(items.map { it.videoId })
        }
    }


    class Factory(private val application: Application) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(application) as T
        }
    }
}