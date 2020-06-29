package com.example.ytaudio.adapter

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


abstract class RecyclerViewAdapter<T, B : ViewDataBinding>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val clickListener: ClickListener<T>
) : ListAdapter<T, RecyclerViewAdapter<T, B>.ViewHolder<B>>(diffCallback) {

    private val _selectedItems = mutableSetOf<T>()
    val selectedItems: Set<T>
        get() = _selectedItems

    private var inActionMode = false

    fun startActionMode() {
        inActionMode = true
        notifyDataSetChanged()
    }

    fun stopActionMode() {
        _selectedItems.clear()
        inActionMode = false
        notifyDataSetChanged()
    }

    fun selectAll() {
        _selectedItems.addAll(currentList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
        holder.bind(getItem(position))
    }


    open inner class ViewHolder<B : ViewDataBinding>(val binding: B) :
        RecyclerView.ViewHolder(binding.root) {

        private fun itemClicked(item: T) {
            if (!_selectedItems.add(item)) {
                _selectedItems.remove(item)
            }
            notifyItemChanged(currentList.indexOf(item))
        }

        open fun bind(item: T) {
            binding.root.apply {
                setBackgroundColor(if (item in _selectedItems) Color.GRAY else Color.TRANSPARENT)

                setOnClickListener {
                    if (!inActionMode) {
                        clickListener.onClick(item)
                    } else {
                        itemClicked(item)
                        clickListener.onActiveModeClick()
                    }
                }

                setOnLongClickListener {
                    if (_selectedItems.isEmpty()) {
                        itemClicked(item)
                    }
                    clickListener.onLongClick(item)
                    true
                }
            }

            binding.executePendingBindings()
        }
    }
}