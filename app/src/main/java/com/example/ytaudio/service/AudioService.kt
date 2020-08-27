package com.example.ytaudio.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.example.ytaudio.domain.PlaylistUseCases
import com.example.ytaudio.service.notification.NotificationManager
import com.example.ytaudio.service.playback.BecomingNoisyReceiver
import com.example.ytaudio.service.playback.PlaybackPreparer
import com.example.ytaudio.service.playback.QueueNavigator
import com.example.ytaudio.utils.Constants.MEDIA_ROOT_ID
import com.example.ytaudio.utils.extensions.flag
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.android.AndroidInjection
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
open class AudioService : MediaBrowserServiceCompat(), Player.EventListener,
    PlayerNotificationManager.NotificationListener {

    @Inject
    lateinit var playlistUseCases: PlaylistUseCases

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

        val sessionActivityPendingIntent =
            packageManager.getLaunchIntentForPackage(packageName)
                .let { PendingIntent.getActivity(this, 0, it, 0) }

        mediaSession = MediaSessionCompat(this, javaClass.simpleName)
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }

        sessionToken = mediaSession.sessionToken

        notificationManager = NotificationManager(this, exoPlayer, mediaSession.sessionToken, this)

        becomingNoisyReceiver =
            BecomingNoisyReceiver(
                this,
                mediaSession.sessionToken
            )

        mediaSessionConnector = MediaSessionConnector(mediaSession).also {
            val dataSourceFactory =
                DefaultDataSourceFactory(this, Util.getUserAgent(this, YTAUDIO_USER_AGENT), null)

            playbackPreparer = PlaybackPreparer(playlistUseCases, exoPlayer, dataSourceFactory)

            it.setPlayer(exoPlayer)
            it.setPlaybackPreparer(playbackPreparer)
            it.setQueueNavigator(QueueNavigator(mediaSession))
        }
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) =
        BrowserRoot(MEDIA_ROOT_ID, null)

    @Synchronized
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaItem>>) {
        val children = playlistUseCases.getMetadata().map {
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
        playbackPreparer.onCancel()
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
        Log.e(javaClass.simpleName, error.toString())
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

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        stopForeground(true)
        isForegroundService = false
        stopSelf()
    }
}


const val NETWORK_FAILURE = "service network failure"
private const val YTAUDIO_USER_AGENT = "ytaudio.next"
