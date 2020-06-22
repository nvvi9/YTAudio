package com.example.ytaudio.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.databinding.ItemPlaylistBinding

class PlaylistAdapter(private val clickListener: AudioInfoListener) :
    ListAdapter<AudioInfo, PlaylistAdapter.ViewHolder>(AudioInfoDiffCallback()) {

    private val _selectedAudioItems = mutableSetOf<AudioInfo>()
    val selectedAudioItems: Set<AudioInfo>
        get() = _selectedAudioItems

    var actionMode = false

    fun selectAll() {
        _selectedAudioItems.addAll(currentList)
        notifyDataSetChanged()
    }

    fun stopActionMode() {
        _selectedAudioItems.clear()
        actionMode = false
        notifyDataSetChanged()
    }

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

        private fun itemClicked(item: AudioInfo) {
            if (!_selectedAudioItems.add(item)) {
                _selectedAudioItems.remove(item)
            }
            notifyDataSetChanged()
        }

        fun bind(clickListener: AudioInfoListener, item: AudioInfo, payloads: MutableList<Any>) {
            var refresh = payloads.isEmpty()

            if (payloads.isNotEmpty()) {
                payloads.forEach {
                    when (it) {
                        PLAYBACK_STATE_CHANGED ->
                            binding.playbackState.setImageResource(item.playbackState)
                        else -> refresh = true
                    }
                }
            }

            binding.itemLayout.setBackgroundColor(if (item in _selectedAudioItems) Color.GRAY else Color.TRANSPARENT)

            if (refresh) {
                binding.audioItem = item
            }

            binding.itemLayout.setOnClickListener {
                if (!actionMode) {
                    clickListener.onClick(item)
                } else {
                    itemClicked(item)
                    clickListener.onActiveModeClick()
                }
            }

            binding.itemLayout.setOnLongClickListener {
                if (_selectedAudioItems.isEmpty()) {
                    itemClicked(item)
                }
                clickListener.onLongClick(item)
                true
            }

            binding.executePendingBindings()
        }
    }
}


class AudioInfoDiffCallback : DiffUtil.ItemCallback<AudioInfo>() {

    override fun areItemsTheSame(oldItem: AudioInfo, newItem: AudioInfo) =
        oldItem.audioId == newItem.audioId

    override fun areContentsTheSame(oldItem: AudioInfo, newItem: AudioInfo) =
        oldItem == newItem

    override fun getChangePayload(oldItem: AudioInfo, newItem: AudioInfo) =
        if (oldItem.playbackState != newItem.playbackState) PLAYBACK_STATE_CHANGED else null
}

interface AudioInfoListener {
    fun onClick(item: AudioInfo)
    fun onLongClick(item: AudioInfo)
    fun onActiveModeClick()
}

const val PLAYBACK_STATE_CHANGED = 1