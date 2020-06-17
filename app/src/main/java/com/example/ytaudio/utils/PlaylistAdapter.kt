package com.example.ytaudio.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.AudioItem
import com.example.ytaudio.databinding.ItemPlaylistBinding

class PlaylistAdapter(private val clickListener: AudioInfoListener) :
    ListAdapter<AudioItem, PlaylistAdapter.ViewHolder>(AudioInfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }


    class ViewHolder private constructor(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: AudioInfoListener, item: AudioItem) {
            binding.audioItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding =
                    ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)

                return ViewHolder(binding)
            }
        }
    }
}


class AudioInfoDiffCallback : DiffUtil.ItemCallback<AudioItem>() {

    override fun areItemsTheSame(oldItem: AudioItem, newItem: AudioItem) =
        oldItem.audioId == newItem.audioId

    override fun areContentsTheSame(oldItem: AudioItem, newItem: AudioItem) =
        oldItem == newItem

    override fun getChangePayload(oldItem: AudioItem, newItem: AudioItem) =
        if (oldItem.playbackStatus != newItem.playbackStatus) 1 else null
}


class AudioInfoListener(val clickListener: (audioItem: AudioItem) -> Unit) {
    fun onClick(audioItem: AudioItem) = clickListener(audioItem)
}