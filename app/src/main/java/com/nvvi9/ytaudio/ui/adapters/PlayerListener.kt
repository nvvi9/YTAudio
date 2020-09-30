package com.nvvi9.ytaudio.ui.adapters


interface PlayerListener {
    fun onPlayPauseClicked()
    fun onSkipToNextClicked()
    fun onSkipToPreviousClicked()
    fun onRepeatButtonClicked()
    fun onShuffleButtonClicked()
    fun onBackButtonClicked()
}