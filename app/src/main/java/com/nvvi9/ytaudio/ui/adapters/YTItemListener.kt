package com.nvvi9.ytaudio.ui.adapters

import com.nvvi9.ytaudio.vo.YouTubeItem


interface YTItemListener {
    fun onItemClicked(videoItem: YouTubeItem)
    fun onItemLongClicked(videoItem: YouTubeItem): Boolean
    fun onItemIconChanged(videoItem: YouTubeItem, newValue: Boolean)
}