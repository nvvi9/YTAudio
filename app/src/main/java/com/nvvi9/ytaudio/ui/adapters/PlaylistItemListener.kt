package com.nvvi9.ytaudio.ui.adapters

import com.nvvi9.ytaudio.vo.PlaylistItem


interface PlaylistItemListener {
    fun onItemClicked(item: PlaylistItem)
    fun onItemLongClicked(item: PlaylistItem): Boolean
}