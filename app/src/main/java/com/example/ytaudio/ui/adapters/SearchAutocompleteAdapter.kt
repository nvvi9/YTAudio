package com.example.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.ItemSearchAutocompleteBinding


class SearchAutocompleteAdapter(private val itemListener: SearchAutocompleteItemListener) :
    ListAdapter<String, SearchAutocompleteAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent, itemListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(
        private val binding: ItemSearchAutocompleteBinding,
        itemListener: SearchAutocompleteItemListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.listener = itemListener
        }

        fun bind(item: String) {
            binding.run {
                data = item
                executePendingBindings()
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                clickListenerSearchItem: SearchAutocompleteItemListener
            ) =
                ItemSearchAutocompleteBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ).let { ViewHolder(it, clickListenerSearchItem) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }
}