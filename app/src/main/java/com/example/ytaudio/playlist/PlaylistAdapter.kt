package com.example.ytaudio.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ytaudio.adapter.ClickListener
import com.example.ytaudio.adapter.RecyclerViewAdapter
import com.example.ytaudio.databinding.ItemPlaylistBinding
import com.example.ytaudio.domain.PlaylistItem


class PlaylistAdapter(clickListener: ClickListener<PlaylistItem>) :
    RecyclerViewAdapter<PlaylistItem, ItemPlaylistBinding>(clickListener) {

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