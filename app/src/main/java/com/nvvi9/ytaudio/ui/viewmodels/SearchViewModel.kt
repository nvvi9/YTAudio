package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvvi9.ytaudio.domain.SearchUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject


class SearchViewModel @Inject constructor(
    private val searchUseCases: SearchUseCases
) : ViewModel() {

    private val _autoComplete = MutableLiveData<List<String>>()
    val autoComplete: LiveData<List<String>>
        get() = _autoComplete

    fun setAutocomplete(query: String) {
        viewModelScope.launch {
            _autoComplete.value = searchUseCases.getAutoCompleteList(query)
        }
    }
}