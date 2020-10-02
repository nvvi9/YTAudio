package com.nvvi9.ytaudio.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvvi9.ytaudio.repositories.SearchRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _autoComplete = MutableLiveData<List<String>>()
    val autoComplete: LiveData<List<String>> = _autoComplete

    fun setAutocomplete(query: String) {
        viewModelScope.launch {
            try {
                _autoComplete.postValue(searchRepository.getAutoComplete(query))
            } catch (t: Throwable) {
                Log.e("SearchViewModel", t.stackTraceToString())
            }
        }
    }
}