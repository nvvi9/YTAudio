package com.example.ytaudio.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ytaudio.adapter.ClickListener
import com.example.ytaudio.adapter.RecyclerViewAdapter
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.databinding.ItemPlaylistBinding


class PlaylistAdapter(clickListener: ClickListener<AudioInfo>) :
    RecyclerViewAdapter<AudioInfo, ItemPlaylistBinding>(clickListener) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<ItemPlaylistBinding> {
        val binding =
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<ItemPlaylistBinding>, position: Int) {
        holder.binding.audioItem = getItem(position)
        super.onBindViewHolder(holder, position)
    }
}