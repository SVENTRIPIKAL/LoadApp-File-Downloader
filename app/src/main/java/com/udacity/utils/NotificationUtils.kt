package com.udacity.utils

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R


/**
 * notification id
 */
const val ZERO = 0
const val ONE = 1
const val TWO = 2
const val THIRTY = 30
const val PAINTER_TEXT_SIZE = 60F
const val BORDER_STROKE_WIDTH = 12F
const val NOTIFICATION_ID = ZERO
const val SUCCESS_RESPONSE_OK = 200
const val FILE_NAME_EXTRA = "fileNameExtra"
const val FILE_STATUS_EXTRA = "fileStatusExtra"


/**
 * check if current API 26+
 */
fun isNotificationChannelRequired() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O


/**
 * Extension function for sending notifications
 * USES-PERMISSION: POST_NOTIFICATIONS
 */
fun NotificationManager.sendNotification(
    messageBody: String, context: Context,
    fileNameExtra: String, fileStatusExtra: Boolean
) {

    // go to file intent -- notification content click
    val goToFileIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)    // bring activity to front if already created
    val goToFilePendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        goToFileIntent,
        PendingIntent.FLAG_IMMUTABLE or
                PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_CANCEL_CURRENT
    )


    // go to detail activity intent -- action button click
    val detailActivityIntent = Intent(context, DetailActivity::class.java)
        .putExtra(NOTIFICATION_ID.toString(), NOTIFICATION_ID)
        .putExtra(FILE_STATUS_EXTRA, fileStatusExtra)
        .putExtra(FILE_NAME_EXTRA, fileNameExtra)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)       // clear stack up to this activity & start new
    val detailActivityPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        detailActivityIntent,
        PendingIntent.FLAG_IMMUTABLE or
                PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_CANCEL_CURRENT
    )


    // notification builder
    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.channel_id_download)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)   // notification icon
        .setContentTitle(context.getString(R.string.notification_title)) // notification title
        .setContentText(messageBody)    // notification message
        .setContentIntent(goToFilePendingIntent)    // notification content click -- redirects to downloads
        .addAction(     // action button click -- redirects to detail screen
            R.drawable.ic_assistant_black_24dp,
            context.getString(R.string.notification_button),
            detailActivityPendingIntent
        )


    // apply build with notification id
    notify(NOTIFICATION_ID, builder.build())
}


/**
 * Create specific channels for broadcasting different notifications
 */
fun createChannel(context: Context, channelId: String, channelName: String) {

    // API 26+ require NotificationChannels
    if (isNotificationChannelRequired()) {

        // create notification channel [id, name, importance]
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        // apply system features for channel notifications
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = context.getString(R.string.channel_description_download)

        // create notification manager & create channel
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
}