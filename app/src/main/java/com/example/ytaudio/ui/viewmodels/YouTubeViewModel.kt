package com.example.ytaudio.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ytaudio.domain.YouTubeUseCases
import com.example.ytaudio.vo.Result
import com.example.ytaudio.vo.Result.Loading
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.launch
import javax.inject.Inject


class YouTubeViewModel @Inject constructor(private val useCases: YouTubeUseCases) : ViewModel() {

    private val _youTubeItemList = MutableLiveData<Result<List<YouTubeItem>>>()
    val youTubeItemList: LiveData<Result<List<YouTubeItem>>>
        get() = _youTubeItemList

    init {
        setRecommended()
    }

    private fun setRecommended() {
        viewModelScope.launch {
            _youTubeItemList.value = Loading()
            _youTubeItemList.value = useCases.getRecommendedYouTubeItems()
        }
    }

    fun setFromQuery(query: String) {
        viewModelScope.launch {
            _youTubeItemList.value = useCases.getYouTubeItemsFromQuery(query)
        }
    }
}