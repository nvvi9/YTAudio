package com.example.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.ItemLoadStateFooterBinding


class YTLoadStateAdapter : LoadStateAdapter<YTLoadStateAdapter.YTLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: YTLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        YTLoadStateViewHolder.create(parent)

    class YTLoadStateViewHolder private constructor(private val binding: ItemLoadStateFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                if ((loadState as? LoadState.Error)?.error?.message.isNullOrBlank()) {
                    errorMsg.visibility = View.GONE
                    errorImage.visibility = View.GONE
                } else {
                    errorMsg.visibility = View.VISIBLE
                    errorImage.visibility = View.VISIBLE
                }
            }
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