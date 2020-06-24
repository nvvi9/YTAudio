package com.example.ytaudio.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ytaudio.network.Response
import com.example.ytaudio.network.YouTubeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private val _response = MutableLiveData<Response>()
    val response: LiveData<Response>
        get() = _response


    init {
        getResponse("rage against the machine", 50)
    }

    fun getResponse(query: String, maxResults: Int = 25) {
        coroutineScope.launch {
            val getResponseDeferred = YouTubeApi.retrofitService.getSearchResponse(query)
            try {
                val resultResponse = getResponseDeferred.await()
                _response.value = resultResponse
            } catch (t: Throwable) {
                Log.i("SearchViewModel", t.message ?: "error")
            }
        }
    }
}