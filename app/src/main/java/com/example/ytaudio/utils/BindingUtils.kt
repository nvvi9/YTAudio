package com.example.ytaudio.utils

import android.text.format.DateUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ytaudio.AudioItem
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.network.VideoItem


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
        Glide.with(context)
            .load(it.photoUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_notification)
                    .error(R.drawable.ic_notification)
            ).into(this)
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

@BindingAdapter("audioPlaylist")
fun RecyclerView.setPlaylist(playlist: List<AudioInfo>?) {
    (adapter as PlaylistAdapter).submitList(playlist?.sortedBy { it.audioTitle })
}

@BindingAdapter("videoItemList")
fun RecyclerView.setVideoItems(videoItemList: List<VideoItem>?) {
    (adapter as VideoItemAdapter).submitList(videoItemList)
}

@BindingAdapter("videoTitle")
fun TextView.setVideoTitle(video: VideoItem?) {
    text = video?.snippet?.title
}

@BindingAdapter("channelTitle")
fun TextView.setAuthor(video: VideoItem?) {
    text = video?.snippet?.channelTitle
}

@BindingAdapter("videoThumbnail")
fun ImageView.setThumbnail(video: VideoItem?) {
    video?.let {
        Glide.with(context)
            .load(it.snippet.thumbnails.high.url)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.photo_white)
                    .error(R.drawable.photo_white)
            )
            .into(this)
    }
}