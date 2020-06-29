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
import com.example.ytaudio.domain.SearchItem
import com.example.ytaudio.playlist.PlaylistAdapter
import com.example.ytaudio.search.SearchAdapter


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
fun ImageView.setPlaybackState(item: PlaylistItem?) {
    item?.let {
        this.setImageResource(it.playbackState)
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

@BindingAdapter("searchItemList")
fun RecyclerView.setSearchItemList(searchItemList: List<SearchItem>?) {
    (adapter as SearchAdapter).submitList(searchItemList)
}

@BindingAdapter("videoTitle")
fun TextView.setVideoTitle(item: SearchItem?) {
    text = item?.title
}

@BindingAdapter("channelTitle")
fun TextView.setChannelTitle(item: SearchItem?) {
    text = item?.channelTitle
}

@BindingAdapter("videoThumbnail")
fun ImageView.setThumbnail(item: SearchItem?) {
    item?.let {
        Glide.with(context)
            .load(it.thumbnailUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.photo_white)
                    .error(R.drawable.photo_white)
            )
            .into(this)
    }
}