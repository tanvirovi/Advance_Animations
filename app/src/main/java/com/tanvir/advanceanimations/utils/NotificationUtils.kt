package com.tanvir.advanceanimations.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.tanvir.advanceanimations.DetailActivity
import com.tanvir.advanceanimations.R

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0
private lateinit var pendingIntent: PendingIntent
private lateinit var action: NotificationCompat.Action
fun NotificationManager.sendNotification(status: String, messageBody: String, applicationContext: Context) {

    val intent = Intent(applicationContext, DetailActivity::class.java)
        .putExtra("status", status)

    pendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    action = NotificationCompat.Action(
        R.drawable.details,
        "open details",
        pendingIntent
    )
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(androidx.loader.R.drawable.notification_bg)
        .setContentIntent(pendingIntent)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(messageBody)
        .setAutoCancel(true)
        .addAction(
            action
        )


    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotification() {
    cancelAll()
}