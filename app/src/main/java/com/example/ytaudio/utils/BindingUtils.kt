package com.example.ytaudio.utils

import android.text.format.DateUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.ytaudio.AudioItem
import com.example.ytaudio.database.AudioInfo


@BindingAdapter("audioTitle")
fun TextView.setTitle(item: AudioItem?) {
    text = item?.title
}


@BindingAdapter("authorName")
fun TextView.setAuthor(item: AudioItem?) {
    text = item?.subtitle
}

@BindingAdapter("audioPhoto")
fun ImageView.setImage(item: AudioItem?) {
    item?.let {
        Glide.with(context).load(it.thumbnailUri).into(this)
    }
}

@BindingAdapter("playbackState")
fun ImageView.setPlaybackState(item: AudioItem?) {
    item?.let {
        this.setImageResource(it.playbackStatus)
    }
}

@BindingAdapter("audioDuration")
fun TextView.setDuration(item: AudioInfo?) {
    text = DateUtils.formatElapsedTime(item?.audioDurationSeconds ?: 0)
}