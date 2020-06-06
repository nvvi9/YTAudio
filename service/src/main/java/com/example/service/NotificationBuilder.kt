package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import com.example.service.extensions.isPlayEnabled
import com.example.service.extensions.isPlaying
import com.example.service.extensions.isSkipToNextEnabled
import com.example.service.extensions.isSkipToPreviousEnabled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


const val NOW_PLAYING_CHANNEL = "com.example.service.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION = 0xb339
private const val MODE_READ_ONLY = "r"


class NotificationBuilder(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    private val skipToPreviousAction = NotificationCompat.Action(
        R.drawable.exo_controls_previous,
        context.getString(R.string.notification_skip_to_previous),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_SKIP_TO_PREVIOUS)
    )

    private val skipToNextAction = NotificationCompat.Action(
        R.drawable.exo_controls_next,
        context.getString(R.string.notification_skip_to_next),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_SKIP_TO_NEXT)
    )
    private val playAction = NotificationCompat.Action(
        R.drawable.exo_controls_play,
        context.getString(R.string.notification_play),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_PLAY)
    )
    private val pauseAction = NotificationCompat.Action(
        R.drawable.exo_controls_pause,
        context.getString(R.string.notification_pause),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_PAUSE)
    )

    private val stopPendingIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
        context,
        ACTION_STOP
    )


    suspend fun buildNotification(sessionToken: MediaSessionCompat.Token): Notification {
        if (shouldCreatePlayingChannel())
            createPlayingChannel()

        val controller = MediaControllerCompat(context, sessionToken)
        val description = controller.metadata.description
        val playbackState = controller.playbackState
        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

        var playPauseIndex = 0

        if (playbackState.isSkipToPreviousEnabled) {
            builder.addAction(skipToPreviousAction)
            ++playPauseIndex
        }
        if (playbackState.isSkipToNextEnabled)
            builder.addAction(skipToNextAction)

        if (playbackState.isPlaying)
            builder.addAction(pauseAction)
        else if (playbackState.isPlayEnabled)
            builder.addAction(playAction)

        val mediaStyle = MediaStyle()
            .setCancelButtonIntent(stopPendingIntent)
            .setMediaSession(sessionToken)
            .setShowActionsInCompactView(playPauseIndex)
            .setShowCancelButton(true)

        val iconBitmap = description.iconUri?.let {
            resolveUriAsBitmap(it)
        }

        return builder.setContentIntent(controller.sessionActivity)
            .setContentText(description.subtitle)
            .setContentTitle(description.title)
            .setDeleteIntent(stopPendingIntent)
            .setLargeIcon(iconBitmap)
            .setOnlyAlertOnce(true)
            .setStyle(mediaStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }


    private fun shouldCreatePlayingChannel() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !playingChannelExists()


    @RequiresApi(Build.VERSION_CODES.O)
    private fun playingChannelExists() =
        notificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPlayingChannel() {
        val notificationChannel = NotificationChannel(
            NOW_PLAYING_CHANNEL,
            context.getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_LOW
        )
            .apply {
                description = context.getString(R.string.notification_channel_description)
            }

        notificationManager.createNotificationChannel(notificationChannel)
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