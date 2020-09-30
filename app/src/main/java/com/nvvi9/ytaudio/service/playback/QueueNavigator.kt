package com.nvvi9.ytaudio.service.playback

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator


class QueueNavigator(mediaSession: MediaSessionCompat) : TimelineQueueNavigator(mediaSession) {

    private val window = Timeline.Window()

    override fun getMediaDescription(player: Player, windowIndex: Int) =
        player.currentTimeline.getWindow(windowIndex, window).tag as MediaDescriptionCompat
}