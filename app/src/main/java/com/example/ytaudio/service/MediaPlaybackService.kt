package com.example.ytaudio.service

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.service.extensions.flag
import com.example.ytaudio.service.library.AudioSource
import com.example.ytaudio.service.library.DatabaseAudioSource
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


open class MediaPlaybackService : MediaBrowserServiceCompat() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var audioSource: AudioSource
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private val database = AudioDatabase.getInstance(applicationContext).audioDatabaseDao
    private val playerListener = PlayerEventListener()

    private var isForegroundService = false

    private val ytAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(this).apply {
            setAudioAttributes(ytAudioAttributes, true)
            addListener(playerListener)
        }
    }

    protected lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()

        val sessionActivityPendingIntent =
            packageManager.getLaunchIntentForPackage(packageName)
                .let {
                    PendingIntent.getActivity(this, 0, it, 0)
                }

        mediaSession = MediaSessionCompat(this, "MediaPlaybackService")
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }

        sessionToken = mediaSession.sessionToken

        notificationManager = NotificationManager(
            this,
            exoPlayer,
            mediaSession.sessionToken,
            PlayerNotificationListener()
        )

        becomingNoisyReceiver = BecomingNoisyReceiver(this, mediaSession.sessionToken)

        audioSource = DatabaseAudioSource(this, database)
        serviceScope.launch {
            audioSource.load()
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession).also {
            val dataSourceFactory =
                DefaultDataSourceFactory(this, Util.getUserAgent(this, YTAUDIO_USER_AGENT), null)

            val playbackPreparer = PlaybackPreparer(audioSource, exoPlayer, dataSourceFactory)

            it.setPlayer(exoPlayer)
            it.setPlaybackPreparer(playbackPreparer)
            it.setQueueNavigator(QueueNavigator(mediaSession))
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? = BrowserRoot(MEDIA_ROOT_ID, null)


    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaItem>>
    ) {
        val resultSent = audioSource.whenReady { initialized ->
            if (initialized) {
                val children = audioSource.map {
                    MediaItem(it.description, it.flag)
                }
                result.sendResult(children as MutableList<MediaItem>?)
            } else {
                mediaSession.sendSessionEvent(NETWORK_FAILURE, null)
                result.sendResult(null)
            }
        }

        if (!resultSent) {
            result.detach()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.run {
            isActive = false
            release()
        }

        serviceJob.cancel()
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    private inner class PlayerEventListener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING, Player.STATE_READY -> {
                    notificationManager.showNotification()
                    becomingNoisyReceiver.register()

                    if (playbackState == Player.STATE_READY) {
                        if (!playWhenReady) {
                            stopForeground(true)
                        }
                    }
                }
                else -> {
                    notificationManager.hideNotification()
                    becomingNoisyReceiver.unregister()
                }
            }
        }
    }


    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MediaPlaybackService.javaClass)
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
}


private class QueueNavigator(mediaSession: MediaSessionCompat) :
    TimelineQueueNavigator(mediaSession) {

    private val window = Timeline.Window()

    override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat =
        player.currentTimeline.getWindow(windowIndex, window, true).tag as MediaDescriptionCompat
}

private class BecomingNoisyReceiver(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token
) : BroadcastReceiver() {

    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val controller = MediaControllerCompat(context, sessionToken)

    private var registered = false


    fun register() {
        if (!registered) {
            context.registerReceiver(this, noisyIntentFilter)
            registered = true
        }
    }


    fun unregister() {
        if (registered) {
            context.unregisterReceiver(this)
            registered = false
        }
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            controller.transportControls.pause()
        }
    }

}

const val NETWORK_FAILURE = "service network failure"
private const val YTAUDIO_USER_AGENT = "ytaudio.next"
private const val MEDIA_ROOT_ID = "media_root_id"
private const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
