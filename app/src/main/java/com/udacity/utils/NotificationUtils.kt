package com.udacity.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.R

private const val NOTIFICATION_ID = 0

/**
 * Extension function for sending notifications
 * USES-PERMISSION: POST_NOTIFICATIONS
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

    /**
     * Intents
     */


    /**
     * Notification builder
     */
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)   // notification icon
        .setContentTitle(applicationContext.getString(R.string.notification_title)) // notification title
        .setContentText(messageBody)    // notification message

    // apply build with notification id
    notify(NOTIFICATION_ID, builder.build())
}


/**
 * Create specific channels for broadcasting different notifications
 */
fun createChannel(context: Context, channelId: String, channelName: String) {

    // API 26+ needed for NotificationChannel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

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
        notificationChannel.description = context.getString(R.string.channel_description)

        // create notification manager & create channel
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
}