package com.example.ytaudio.utils

import android.text.format.DateUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.ytaudio.AudioItem
import com.example.ytaudio.database.AudioInfo


@BindingAdapter("audioTitle")
fun TextView.setTitle(item: AudioInfo?) {
    text = item?.audioTitle
}


@BindingAdapter("authorName")
fun TextView.setAuthor(item: AudioInfo?) {
    text = item?.author
}

@BindingAdapter("audioPhoto")
fun ImageView.setImage(item: AudioInfo?) {
    item?.let {
        Glide.with(context).load(it.photoUrl).into(this)
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