package com.example.ytaudio.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.ytaudio.adapter.ClickListener
import com.example.ytaudio.adapter.RecyclerViewAdapter
import com.example.ytaudio.databinding.SearchItemBinding
import com.example.ytaudio.domain.SearchItem


class SearchAdapter(clickListener: ClickListener<SearchItem>) :
    RecyclerViewAdapter<SearchItem, SearchItemBinding>(DiffCallback(), clickListener) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<SearchItemBinding> {
        val binding =
            SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<SearchItemBinding>, position: Int) {
        holder.binding.searchItem = getItem(position)
        super.onBindViewHolder(holder, position)
    }
}


private class DiffCallback : DiffUtil.ItemCallback<SearchItem>() {

    override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem) =
        oldItem.videoId == newItem.videoId

    override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem) =
        oldItem == newItem
}