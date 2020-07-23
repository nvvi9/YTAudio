package com.example.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.ItemSearchAutocompleteBinding


class SearchAutocompleteAdapter(private val clickListener: AutocompleteAdapterClickListener) :
    ListAdapter<String, SearchAutocompleteAdapter.ViewHolder>(StringDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent, clickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemSearchAutocompleteBinding,
        private val clickListener: AutocompleteAdapterClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.apply {
                data = item
                fillSearchViewButton.setOnClickListener { clickListener.arrowClicked(item) }
                root.setOnClickListener { clickListener.itemClicked(item) }
                executePendingBindings()
            }
        }

        companion object {
            fun create(parent: ViewGroup, clickListener: AutocompleteAdapterClickListener) =
                ItemSearchAutocompleteBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ).let { ViewHolder(it, clickListener) }
        }
    }
}


class StringDiffCallback : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem
}


class AutocompleteAdapterClickListener(
    private val onItemClicked: (text: String) -> Unit,
    private val onArrowClicked: (text: String) -> Unit
) {
    fun itemClicked(text: String) = onItemClicked(text)
    fun arrowClicked(text: String) = onArrowClicked(text)
}