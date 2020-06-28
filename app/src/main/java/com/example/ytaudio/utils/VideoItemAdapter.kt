package com.example.ytaudio.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.VideoItemBinding
import com.example.ytaudio.network.youtube.VideoItem

class VideoItemAdapter(private val clickListener: VideoItemListener) :
    ListAdapter<VideoItem, VideoItemAdapter.ViewHolder>(VideoItemDiffCallback()) {

    private val _selectedItems = mutableSetOf<VideoItem>()
    val selectedItems: Set<VideoItem>
        get() = _selectedItems

    private var inActionMode = false

    fun selectAll() {
        _selectedItems.addAll(currentList)
        notifyDataSetChanged()
    }

    fun startActionMode() {
        inActionMode = true
        notifyDataSetChanged()
    }

    fun stopActionMode() {
        _selectedItems.clear()
        inActionMode = false
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))


    inner class ViewHolder(private val binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun itemClicked(item: VideoItem) {
            if (!_selectedItems.add(item)) {
                _selectedItems.remove(item)
            }
            notifyDataSetChanged()
        }

        fun bind(item: VideoItem) {
            binding.apply {

                root.setBackgroundColor(if (item in _selectedItems) Color.GRAY else Color.TRANSPARENT)

                videoItem = item

                root.setOnClickListener {
                    if (!inActionMode) {
                        clickListener.onClick(item)
                    } else {
                        itemClicked(item)
                        clickListener.onActiveModeClick()
                    }

                }

                root.setOnLongClickListener {
                    if (_selectedItems.isEmpty()) {
                        itemClicked(item)
                    }
                    clickListener.onLongClick(item)
                    true
                }

                executePendingBindings()
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

abstract class VideoItemListener : ClickListener<VideoItem> {
    abstract fun onActiveModeClick()
}

