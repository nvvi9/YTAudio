package com.example.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.ItemPlaylistBinding
import com.example.ytaudio.vo.PlaylistItem


class PlaylistItemAdapter {

    class ViewHolder(
        private val binding: ItemPlaylistBinding,
        private val clickListener: PlaylistItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlaylistItem) {
            binding.apply {
                audioItem = item
            }
        }

        companion object {
            fun create(parent: ViewGroup, clickListener: PlaylistItemClickListener) =
                ViewHolder(
                    ItemPlaylistBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), clickListener
                )
        }
    }
}


class PlaylistItemClickListener(
    private val onItemClicked: (item: PlaylistItem) -> Unit,
    private val onItemLongClicked: (item: PlaylistItem) -> Unit
) {
    fun onClick(item: PlaylistItem) = onItemClicked(item)
    fun onLongClick(item: PlaylistItem) = onItemLongClicked(item)
}