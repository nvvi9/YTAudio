package com.example.ytaudio.adapter

import android.text.format.DateUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ytaudio.R
import com.example.ytaudio.domain.PlaylistItem
import com.example.ytaudio.network.youtube.VideoItem
import com.example.ytaudio.playlist.PlaylistAdapter
import com.example.ytaudio.search.VideoItemAdapter
import com.example.ytaudio.utils.AudioItem


@BindingAdapter("audioTitle")
fun TextView.setTitle(item: PlaylistItem?) {
    text = item?.title
}


@BindingAdapter("authorName")
fun TextView.setAuthor(item: PlaylistItem?) {
    text = item?.author
}

@BindingAdapter("audioPhoto")
fun ImageView.setImage(item: PlaylistItem?) {
    item?.let {
        Glide.with(context)
            .load(it.thumbnailUri)
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
fun TextView.setDuration(item: PlaylistItem?) {
    text = DateUtils.formatElapsedTime(item?.duration ?: 0)
}

@BindingAdapter("audioPlaylist")
fun RecyclerView.setPlaylist(playlist: List<PlaylistItem>?) {
    (adapter as PlaylistAdapter).submitList(playlist?.sortedBy { it.title })
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