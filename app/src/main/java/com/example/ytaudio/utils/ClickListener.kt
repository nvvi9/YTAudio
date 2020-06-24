package com.example.ytaudio.utils

interface ClickListener<T> {
    fun onClick(item: T)
    fun onLongClick(item: T)
}