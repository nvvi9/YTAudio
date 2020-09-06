package com.example.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.ItemPlaylistBinding
import com.example.ytaudio.vo.PlaylistItem


class PlaylistAdapter(private val clickListener: PlaylistClickListener) :
    ListAdapter<PlaylistItem, PlaylistAdapter.ViewHolder>(PlaylistItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent, clickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(
        private val binding: ItemPlaylistBinding,
        private val clickListener: PlaylistClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlaylistItem) {
            binding.run {
                audioItem = item
                listener = clickListener
                executePendingBindings()
            }
        }

        companion object {
            fun create(parent: ViewGroup, clickListener: PlaylistClickListener) =
                ViewHolder(
                    ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    clickListener
                )
        }
    }
}


class PlaylistClickListener(
    val onItemClicked: (item: PlaylistItem) -> Unit,
    val onItemLongClicked: (item: PlaylistItem) -> Boolean
)


object PlaylistItemDiffCallback : DiffUtil.ItemCallback<PlaylistItem>() {

    override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem) =
        oldItem == newItem
}