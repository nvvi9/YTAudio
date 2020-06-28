package com.example.ytaudio.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ytaudio.adapter.ClickListener
import com.example.ytaudio.adapter.RecyclerViewAdapter
import com.example.ytaudio.databinding.VideoItemBinding
import com.example.ytaudio.network.youtube.VideoItem


class VideoItemAdapter(clickListener: ClickListener<VideoItem>) :
    RecyclerViewAdapter<VideoItem, VideoItemBinding>(clickListener) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<VideoItemBinding> {
        val binding =
            VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<VideoItemBinding>, position: Int) {
        holder.binding.videoItem = getItem(position)
        super.onBindViewHolder(holder, position)
    }
}
