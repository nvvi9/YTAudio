package com.example.ytaudio.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ytaudio.domain.YouTubeUseCases
import javax.inject.Inject



class SearchResultsViewModel @Inject constructor(
    private val useCases: YouTubeUseCases
) : ViewModel() {

    fun getFromQuery(query: String) =
        useCases.getYouTubeItemsFromQuery(query)
}