package com.example.ytaudio.utils.extensions

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.updateFields(action: T.() -> Unit) {
    value = value.also {
        it?.action()
    }
}

fun <T> MutableLiveData<T>.postValueUpdating(action: T.() -> Unit) {
    postValue(value.also {
        it?.action()
    })
}