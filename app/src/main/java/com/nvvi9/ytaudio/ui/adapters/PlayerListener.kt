package com.nvvi9.ytaudio.ui.adapters


class PlayerListener(
    private val playPauseClicked: (id: String) -> Unit,
    private val skipToNextClicked: () -> Unit,
    private val skipToPreviousClicked: () -> Unit
) {
    fun onPlayPauseClicked(id: String) = playPauseClicked(id)
    fun onSkipToNextClicked() = skipToNextClicked
    fun onSkipToPreviousClicked() = skipToPreviousClicked
}