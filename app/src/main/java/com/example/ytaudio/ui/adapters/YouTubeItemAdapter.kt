package com.example.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.ytaudio.databinding.ItemYoutubeBinding
import com.example.ytaudio.vo.YouTubeItem


class YouTubeItemsAdapter(private val listener: YTItemAdapterListener) :
    PagingDataAdapter<YouTubeItem, YTItemViewHolder>(YouTubeItemDiffCallback()) {

    override fun onBindViewHolder(holder: YTItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YTItemViewHolder =
        YTItemViewHolder(
            ItemYoutubeBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener
        )
}


private class YouTubeItemDiffCallback : DiffUtil.ItemCallback<YouTubeItem>() {

    override fun areItemsTheSame(oldItem: YouTubeItem, newItem: YouTubeItem): Boolean =
        oldItem.videoId == newItem.videoId

    override fun areContentsTheSame(oldItem: YouTubeItem, newItem: YouTubeItem): Boolean =
        oldItem == newItem
}