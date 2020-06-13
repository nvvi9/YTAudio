package com.example.ytaudio.service

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.example.ytaudio.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.*


class NotificationManager(
    private val context: Context,
    private val player: ExoPlayer,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
) {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)

        notificationManager = PlayerNotificationManager(
            context,
            NOW_PLAYING_CHANNEL,
            NOW_PLAYING_NOTIFICATION,
            DescriptionAdapter(mediaController),
            notificationListener
        ).apply {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.ic_notification)
            setRewindIncrementMs(0)
            setFastForwardIncrementMs(0)
        }
    }


    fun hideNotification() {
        notificationManager.setPlayer(null)
    }


    fun showNotification() {
        notificationManager.setPlayer(player)
    }


    private inner class DescriptionAdapter(private val controller: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var bitmap: Bitmap? = null

        override fun createCurrentContentIntent(player: Player?): PendingIntent? =
            controller.sessionActivity

        override fun getCurrentContentText(player: Player?) =
            controller.metadata.description.subtitle.toString()

        override fun getCurrentContentTitle(player: Player?) =
            controller.metadata.description.title.toString()

        override fun getCurrentLargeIcon(
            player: Player?,
            callback: PlayerNotificationManager.BitmapCallback?
        ): Bitmap? {
            val iconUri = controller.metadata.description.iconUri

            return if (currentIconUri != iconUri || bitmap == null) {
                currentIconUri = iconUri
                serviceScope.launch {
                    bitmap = iconUri?.let {
                        resolveUriAsBitmap(it)
                    }
                    callback?.onBitmap(bitmap)
                }
                null
            } else {
                bitmap
            }
        }

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                val parcelFileDescriptor =
                    context.contentResolver.openFileDescriptor(uri, MODE_READ_ONLY)
                        ?: return@withContext null

                val fileDescriptor = parcelFileDescriptor.fileDescriptor

                BitmapFactory.decodeFileDescriptor(fileDescriptor).apply {
                    parcelFileDescriptor.close()
                }
            }
        }
    }
}

const val NOW_PLAYING_CHANNEL = "channel"
const val NOW_PLAYING_NOTIFICATION = 111
private const val MODE_READ_ONLY = "r"