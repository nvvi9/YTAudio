package com.nvvi9.ytaudio.service.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.utils.Constants.NOW_PLAYING_CHANNEL
import com.nvvi9.ytaudio.utils.Constants.NOW_PLAYING_NOTIFICATION
import kotlinx.coroutines.*


class NotificationManager(
    private val context: Context,
    private val player: ExoPlayer,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
) {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val playerNotificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            NOW_PLAYING_CHANNEL,
            R.string.app_name,
            NOW_PLAYING_NOTIFICATION,
            DescriptionAdapter(mediaController),
            notificationListener
        ).apply {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.ic_audiotrack)
            setRewindIncrementMs(0)
            setFastForwardIncrementMs(0)
        }
    }

    fun hideNotification() {
        playerNotificationManager.setPlayer(null)
    }

    fun showNotification() {
        playerNotificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(private val controller: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var bitmap: Bitmap? = null


        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controller.sessionActivity

        override fun getCurrentContentText(player: Player) =
            controller.metadata.description.subtitle.toString()

        override fun getCurrentContentTitle(player: Player) =
            controller.metadata.description.title.toString()

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = controller.metadata.description.iconUri

            return if (currentIconUri != iconUri || bitmap == null) {
                currentIconUri = iconUri
                serviceScope.launch {
                    bitmap = iconUri?.let { resolveUriAsBitmapAsync(it) }
                    bitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                bitmap
            }
        }

        private suspend fun resolveUriAsBitmapAsync(uri: Uri): Bitmap? =
            withContext(Dispatchers.IO) {
                try {
                    Glide.with(context).asBitmap()
                        .load(uri).apply(
                            RequestOptions()
                                .placeholder(R.drawable.ic_audiotrack)
                                .error(R.drawable.ic_audiotrack)
                        )
                        .submit()
                        .get()
                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, e.toString())
                    null
                }
            }
    }
}