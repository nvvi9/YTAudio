package com.example.ytaudio.adapter


interface ClickListener<T> {
    fun onClick(item: T)
    fun onLongClick(item: T)
    fun onActiveModeClick()
}