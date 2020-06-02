package com.example.ytaudio.screens.playlist

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.ytaudio.database.AudioInfo

@BindingAdapter("audioTitle")
fun TextView.setTitle(item: AudioInfo?) {
    text = item?.audioTitle
}


@BindingAdapter("audioPhoto")
fun ImageView.setImage(item: AudioInfo?) {
    item?.let {
        Glide.with(context).load(it.photoUri).into(this)
    }
}