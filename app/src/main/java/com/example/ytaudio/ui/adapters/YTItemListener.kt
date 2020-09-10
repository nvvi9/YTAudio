package com.example.ytaudio.ui.adapters

import android.view.View
import com.example.ytaudio.vo.YouTubeItem


interface YTItemListener {
    fun onItemClicked(cardView: View, items: YouTubeItem)
    fun onItemIconChanged(item: YouTubeItem, newValue: Boolean)
}