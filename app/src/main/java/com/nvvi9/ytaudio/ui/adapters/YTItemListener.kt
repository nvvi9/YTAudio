package com.nvvi9.ytaudio.ui.adapters

import android.view.View
import com.nvvi9.ytaudio.vo.YouTubeItem


interface YTItemListener {
    fun onItemClicked(cardView: View, item: YouTubeItem)
    fun onItemLongClicked(item: YouTubeItem): Boolean
    fun onItemIconChanged(item: YouTubeItem, newValue: Boolean)
}