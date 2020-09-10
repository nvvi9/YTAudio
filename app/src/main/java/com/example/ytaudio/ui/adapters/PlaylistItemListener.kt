package com.example.ytaudio.ui.adapters

import com.example.ytaudio.vo.PlaylistItem


interface PlaylistItemListener {
    fun onItemClicked(item: PlaylistItem)
    fun onItemLongClicked(item: PlaylistItem): Boolean
}