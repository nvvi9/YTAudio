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

    var inActionMode = false

    fun selectAll() {
        _selectedAudioItems.addAll(currentList)
        notifyDataSetChanged()
    }

    fun startActionMode() {
        inActionMode = true
        notifyDataSetChanged()
    }

    fun stopActionMode() {
        _selectedAudioItems.clear()
        inActionMode = false
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(clickListener, getItem(position))


    inner class ViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun itemClicked(item: AudioInfo) {
            if (!_selectedAudioItems.add(item)) {
                _selectedAudioItems.remove(item)
            }
            notifyDataSetChanged()
        }

        fun bind(clickListener: AudioInfoListener, item: AudioInfo) {
            binding.apply {
                audioItem = item
                root.setBackgroundColor(if (item in _selectedAudioItems) Color.GRAY else Color.TRANSPARENT)

                root.setOnClickListener {
                    if (!inActionMode) {
                        clickListener.onClick(item)
                    } else {
                        itemClicked(item)
                        clickListener.onActiveModeClick()
                    }
                }

                root.setOnLongClickListener {
                    if (_selectedAudioItems.isEmpty()) {
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


class AudioInfoDiffCallback : DiffUtil.ItemCallback<AudioInfo>() {

    override fun areItemsTheSame(oldItem: AudioInfo, newItem: AudioInfo) =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: AudioInfo, newItem: AudioInfo) =
        oldItem == newItem
}


abstract class AudioInfoListener : ClickListener<AudioInfo> {
    abstract fun onActiveModeClick()
}
