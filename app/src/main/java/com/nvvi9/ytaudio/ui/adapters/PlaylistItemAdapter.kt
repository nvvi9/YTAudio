package com.nvvi9.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nvvi9.ytaudio.databinding.ItemPlaylistBinding
import com.nvvi9.ytaudio.vo.PlaylistItem


class PlaylistItemAdapter(private val itemListener: PlaylistItemListener) :
    ListAdapter<PlaylistItem, PlaylistItemAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent, itemListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(
        private val binding: ItemPlaylistBinding,
        itemListener: PlaylistItemListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.listener = itemListener
        }

        fun bind(item: PlaylistItem) {
            binding.run {
                playlistItem = item
                executePendingBindings()
            }
        }

        companion object {
            fun create(parent: ViewGroup, itemListener: PlaylistItemListener) =
                ViewHolder(
                    ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    itemListener
                )
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<PlaylistItem>() {

        override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem) =
            oldItem == newItem
    }
}