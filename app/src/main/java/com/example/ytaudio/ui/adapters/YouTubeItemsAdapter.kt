package com.example.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.ItemYoutubeBinding
import com.example.ytaudio.vo.YouTubeItem

class YouTubeItemsAdapter(private val onClick: (YouTubeItem) -> Unit) :
    ListAdapter<YouTubeItem, YouTubeItemsAdapter.ViewHolder>(YouTubeItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemYoutubeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder.from(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }


    class ViewHolder private constructor(private val binding: ItemYoutubeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: YouTubeItem, onClick: (YouTubeItem) -> Unit) {
            binding.youTubeItem = item
            binding.root.setOnClickListener { onClick(item) }
            binding.executePendingBindings()
        }


        companion object {

            fun from(binding: ItemYoutubeBinding) =
                ViewHolder(binding)
        }
    }
}


private class YouTubeItemDiffCallback : DiffUtil.ItemCallback<YouTubeItem>() {

    override fun areItemsTheSame(oldItem: YouTubeItem, newItem: YouTubeItem): Boolean =
        oldItem.videoId == newItem.videoId

    override fun areContentsTheSame(oldItem: YouTubeItem, newItem: YouTubeItem): Boolean =
        oldItem == newItem
}