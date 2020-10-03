package com.nvvi9.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nvvi9.ytaudio.databinding.ItemLoadStateFooterBinding


class YTLoadStateAdapter : LoadStateAdapter<YTLoadStateAdapter.YTLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: YTLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        YTLoadStateViewHolder.create(parent)


    class YTLoadStateViewHolder private constructor(private val binding: ItemLoadStateFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            binding.progressBar.isVisible = loadState is LoadState.Loading
        }

        companion object {

            fun create(parent: ViewGroup) =
                YTLoadStateViewHolder(
                    ItemLoadStateFooterBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
        }
    }
}