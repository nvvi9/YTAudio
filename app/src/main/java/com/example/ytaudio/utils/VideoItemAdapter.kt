package com.example.ytaudio.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.VideoItemBinding
import com.example.ytaudio.network.VideoItem

class VideoItemAdapter(private val clickListener: ClickListener<VideoItem>) :
    ListAdapter<VideoItem, VideoItemAdapter.ViewHolder>(VideoItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), clickListener)


    class ViewHolder private constructor(val binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VideoItem, clickListener: ClickListener<VideoItem>) {
            binding.apply {
                videoItem = item
                root.setOnClickListener {
                    clickListener.onClick(item)
                }

                root.setOnLongClickListener {
                    clickListener.onLongClick(item)
                    true
                }

                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding =
                    VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

                return ViewHolder(binding)
            }
        }
    }
}


class VideoItemDiffCallback : DiffUtil.ItemCallback<VideoItem>() {

    override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem) =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem) =
        oldItem == newItem
}
