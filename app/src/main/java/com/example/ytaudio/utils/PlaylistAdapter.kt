package com.example.ytaudio.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.AudioItem
import com.example.ytaudio.databinding.ItemPlaylistBinding

class PlaylistAdapter(private val clickListener: AudioInfoListener) :
    ListAdapter<AudioItem, PlaylistAdapter.ViewHolder>(AudioInfoDiffCallback()) {

    val selectedAudioItems = mutableSetOf<AudioItem>()
    var actionMode = false

    fun selectAll() {
        selectedAudioItems.addAll(currentList)
    }

    fun clearSelected() =
        selectedAudioItems.clear()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.bind(clickListener, getItem(position), payloads)
    }

    inner class ViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun itemClicked(item: AudioItem) {
            if (selectedAudioItems.add(item)) {
                binding.itemLayout.setBackgroundColor(Color.GRAY)
            } else {
                selectedAudioItems.remove(item)
                binding.itemLayout.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        fun bind(clickListener: AudioInfoListener, item: AudioItem, payloads: MutableList<Any>) {
            var refresh = payloads.isEmpty()

            if (payloads.isNotEmpty()) {
                payloads.forEach {
                    when (it) {
                        PLAYBACK_STATE_CHANGED ->
                            binding.playbackState.setImageResource(item.playbackStatus)
                        else -> refresh = true
                    }
                }
            }

            if (refresh) {
                binding.audioItem = item
            }

            binding.itemLayout.setOnClickListener {
                if (!actionMode) {
                    clickListener.onClick(item)
                } else {
                    itemClicked(item)
                }
            }

            binding.itemLayout.setOnLongClickListener {
                clickListener.onLongClick(item)
                itemClicked(item)
                true
            }

            binding.executePendingBindings()
        }
    }
}


class AudioInfoDiffCallback : DiffUtil.ItemCallback<AudioItem>() {

    override fun areItemsTheSame(oldItem: AudioItem, newItem: AudioItem) =
        oldItem.audioId == newItem.audioId

    override fun areContentsTheSame(oldItem: AudioItem, newItem: AudioItem) =
        oldItem == newItem

    override fun getChangePayload(oldItem: AudioItem, newItem: AudioItem) =
        if (oldItem.playbackStatus != newItem.playbackStatus) PLAYBACK_STATE_CHANGED else null
}

interface AudioInfoListener {
    fun onClick(item: AudioItem)
    fun onLongClick(item: AudioItem)
}

const val PLAYBACK_STATE_CHANGED = 1