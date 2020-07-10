package com.example.ytaudio.adapters

import android.view.ActionMode
import android.view.Menu
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.R
import com.example.ytaudio.fragment.ActionModeFragment


abstract class RecyclerViewAdapter<T, B : ViewDataBinding>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val fragment: ActionModeFragment,
    private val clickListener: (T) -> Unit
) : ListAdapter<T, RecyclerViewAdapter<T, B>.ViewHolder<B>>(diffCallback), ActionMode.Callback {

    protected val selectedItems = mutableSetOf<T>()
    private var inActionMode = false

    @CallSuper
    override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
        holder.bind(getItem(position))
    }

    final override fun onDestroyActionMode(mode: ActionMode?) {
        stopActionMode()
        mode?.finish()
        fragment.actionMode = null
    }

    final override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

    fun startActionMode() {
        if (fragment.actionMode == null || !inActionMode) {
            inActionMode = true
            fragment.actionMode =
                fragment.activity?.startActionMode(this)
            fragment.actionMode?.title =
                fragment.getString(R.string.selected_items, selectedItems.size)

            notifyDataSetChanged()
        }
    }

    protected fun stopActionMode() {
        selectedItems.clear()
        inActionMode = false
        fragment.actionMode?.finish()
        fragment.actionMode = null
        notifyDataSetChanged()
    }

    protected fun selectAll() {
        selectedItems.addAll(currentList)
        notifyDataSetChanged()
    }

    inner class ViewHolder<B : ViewDataBinding>(val binding: B) :
        RecyclerView.ViewHolder(binding.root) {

        private fun itemClicked(item: T) {
            if (!selectedItems.add(item)) {
                selectedItems.remove(item)
            }
            notifyItemChanged(currentList.indexOf(item))
        }

        fun bind(item: T) {
            binding.root.apply {
                isActivated = item in selectedItems

                setOnClickListener {
                    if (!inActionMode) {
                        clickListener(item)
                    } else {
                        itemClicked(item)
                        val selectedItemsSize = selectedItems.size
                        if (selectedItemsSize != 0) {
                            fragment.actionMode?.title =
                                fragment.getString(R.string.selected_items, selectedItemsSize)
                        } else {
                            stopActionMode()
                        }
                    }
                }

                setOnLongClickListener {
                    if (selectedItems.isEmpty()) {
                        itemClicked(item)
                    }
                    startActionMode()
                    true
                }
            }

            binding.executePendingBindings()
        }
    }
}