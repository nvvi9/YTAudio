package com.nvvi9.ytaudio.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.nvvi9.ytaudio.domain.AudioInfoUseCase
import com.nvvi9.ytaudio.service.notification.NotificationManager
import com.nvvi9.ytaudio.service.playback.BecomingNoisyReceiver
import com.nvvi9.ytaudio.service.playback.PlaybackPreparer
import com.nvvi9.ytaudio.service.playback.QueueNavigator
import com.nvvi9.ytaudio.utils.Constants.MEDIA_ROOT_ID
import com.nvvi9.ytaudio.utils.Constants.YTAUDIO_USER_AGENT
import com.nvvi9.ytaudio.utils.extensions.flag
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Singleton


@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
open class AudioService :
    MediaBrowserServiceCompat(),
    Player.EventListener,
    PlayerNotificationManager.NotificationListener {

    @Inject
    lateinit var audioInfoUseCase: AudioInfoUseCase

    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    private lateinit var playbackPreparer: PlaybackPreparer
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var mediaSession: MediaSessionCompat

    private var isForegroundService = false

    private val ytAudioAttributes =
        AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

    private val exoPlayer: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build().apply {
            setAudioAttributes(ytAudioAttributes, true)
            addListener(this@AudioService)
        }
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()

        mediaSession = MediaSessionCompat(this, javaClass.simpleName).apply {
            setSessionActivity(
                PendingIntent.getActivity(
                    this@AudioService, 0, packageManager.getLaunchIntentForPackage(packageName), 0
                )
            )
            isActive = true
        }
        sessionToken = mediaSession.sessionToken
        notificationManager =
            NotificationManager(this, exoPlayer, ioDispatcher, mediaSession.sessionToken, this)
        becomingNoisyReceiver = BecomingNoisyReceiver(this, mediaSession.sessionToken)
        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            playbackPreparer = PlaybackPreparer(
                audioInfoUseCase, exoPlayer, DefaultDataSourceFactory(
                    this@AudioService,
                    Util.getUserAgent(this@AudioService, YTAUDIO_USER_AGENT),
                    null
                )
            )

            setPlayer(exoPlayer)
            setPlaybackPreparer(playbackPreparer)
            setQueueNavigator(QueueNavigator(mediaSession))
        }
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) =
        BrowserRoot(MEDIA_ROOT_ID, null)

    @Synchronized
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaItem>>) {
        val children = audioInfoUseCase.getMetadata().map {
            MediaItem(it.description, it.flag)
        }

        result.sendResult(children as MutableList<MediaItem>?)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.apply {
            isActive = false
            release()
        }

        exoPlayer.removeListener(this)
        exoPlayer.release()
        playbackPreparer.cancel()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING, Player.STATE_READY -> {
                notificationManager.showNotification()
                becomingNoisyReceiver.register()

                if (playbackState == Player.STATE_READY && !playWhenReady) {
                    stopForeground(false)
                }
            }
            else -> {
                notificationManager.hideNotification()
                becomingNoisyReceiver.unregister()
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        Log.e(javaClass.simpleName, error.stackTraceToString())
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        if (ongoing && !isForegroundService) {
            ContextCompat.startForegroundService(
                applicationContext,
                Intent(applicationContext, this@AudioService.javaClass)
            )

            startForeground(notificationId, notification)
            isForegroundService = true
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        exoPlayer.repeatMode = repeatMode
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        exoPlayer.shuffleModeEnabled = shuffleModeEnabled
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        stopForeground(true)
        isForegroundService = false
        stopSelf()
    }
}