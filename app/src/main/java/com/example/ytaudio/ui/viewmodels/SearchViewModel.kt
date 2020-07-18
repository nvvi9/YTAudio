package com.example.ytaudio.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ytaudio.repositories.SearchRepository
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.launch
import javax.inject.Inject


class SearchViewModel @Inject constructor(private val repository: SearchRepository) : ViewModel() {

    val searchItemList = repository.youTubeItemList
    val autoComplete = repository.autoCompleteList

    fun setResponse(query: String) {
        viewModelScope.launch {
            try {
                this@SearchViewModel.repository.setItemsFromResponse(query)
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

    fun insertInDatabase(items: List<YouTubeItem>) {
        viewModelScope.launch {
            repository.addDatabase(items)
        }
    }
}