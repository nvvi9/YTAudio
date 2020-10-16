package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvvi9.ytaudio.data.datatype.Result
import com.nvvi9.ytaudio.domain.SearchUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject


class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _autoComplete = MutableLiveData<List<String>>()
    val autoComplete: LiveData<List<String>> = _autoComplete

    fun setAutocomplete(query: String) {
        viewModelScope.launch(ioDispatcher) {
            (searchUseCase.getSuggestionList(query) as? Result.Success)?.data?.let {
                _autoComplete.postValue(it)
            }
        }
    }
}