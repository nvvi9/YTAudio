package com.example.ytaudio.search

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import com.example.ytaudio.R
import com.example.ytaudio.adapters.RecyclerViewAdapter
import com.example.ytaudio.databinding.SearchItemBinding
import com.example.ytaudio.domain.SearchItem


class SearchAdapter(
    private val fragment: SearchFragment,
    clickListener: (SearchItem) -> Unit
) : RecyclerViewAdapter<SearchItem, SearchItemBinding>(DiffCallback(), fragment, clickListener) {

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.action_add -> {
                fragment.viewModel.insertInDatabase(selectedItems.toList())
                stopActionMode()
                true
            }
            else -> false
        }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) =
        mode?.run {
            menuInflater.inflate(R.menu.search_toolbar_action_mode, menu)
            true
        } ?: false

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