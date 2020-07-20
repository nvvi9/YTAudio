package com.example.ytaudio.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.ItemYoutubeBinding
import com.example.ytaudio.vo.YouTubeItem

class YouTubeItemViewHolder private constructor(
    private val binding: ItemYoutubeBinding,
    private val onClick: (item: YouTubeItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: YouTubeItem?) {
        item?.let {
            binding.youTubeItem = item
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    companion object {

        fun create(binding: ItemYoutubeBinding, onClick: (item: YouTubeItem) -> Unit) =
            YouTubeItemViewHolder(binding, onClick)
    }
}