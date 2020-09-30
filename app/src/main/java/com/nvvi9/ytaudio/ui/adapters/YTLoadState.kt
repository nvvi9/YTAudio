package com.nvvi9.ytaudio.ui.adapters


sealed class YTLoadState {
    object Empty : YTLoadState()
    object Loading : YTLoadState()
    object LoadingDone : YTLoadState()
    object Error : YTLoadState()
}