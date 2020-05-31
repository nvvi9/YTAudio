package com.example.ytaudio.screens.playlist

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.ytaudio.database.AudioInfo

@BindingAdapter("audioTitle")
fun TextView.setTitle(item: AudioInfo?) {
    text = item?.audioTitle
}