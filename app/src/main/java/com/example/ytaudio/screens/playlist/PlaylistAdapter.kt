package com.example.ytaudio.screens.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.databinding.ItemPlaylistBinding

class PlaylistAdapter(val clickListener: AudioInfoListener) :
    ListAdapter<AudioInfo, PlaylistAdapter.ViewHolder>(AudioInfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }


    class ViewHolder private constructor(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: AudioInfoListener, item: AudioInfo) {
            binding.audio = item
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


class AudioInfoDiffCallback : DiffUtil.ItemCallback<AudioInfo>() {

    override fun areItemsTheSame(oldItem: AudioInfo, newItem: AudioInfo) =
        oldItem.audioId == newItem.audioId


    override fun areContentsTheSame(oldItem: AudioInfo, newItem: AudioInfo) =
        oldItem == newItem
}


class AudioInfoListener(val clickListener: (audioUri: String, audioTitle: String, photoUri: String) -> Unit) {
    fun onClick(audio: AudioInfo) = clickListener(audio.audioUri, audio.audioTitle, audio.photoUri)
}