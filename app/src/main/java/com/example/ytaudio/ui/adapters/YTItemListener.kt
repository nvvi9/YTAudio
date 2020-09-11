package com.example.ytaudio.ui.adapters

import android.view.View
import com.example.ytaudio.vo.YouTubeItem


interface YTItemListener {
    fun onItemClicked(cardView: View, item: YouTubeItem)
    fun onItemLongClicked(item: YouTubeItem): Boolean
    fun onItemIconChanged(item: YouTubeItem, newValue: Boolean)
}