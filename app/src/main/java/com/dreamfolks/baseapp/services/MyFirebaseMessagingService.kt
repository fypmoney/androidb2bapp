package com.dreamfolks.baseapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.util.SharedPrefUtils
import com.dreamfolks.baseapp.view.activity.PushNotificationView
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.annotations.NotNull


/*
*  This class is used to handle the push notifications in the application
* */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("FCMToken", s)
        SharedPrefUtils.putString(applicationContext, SharedPrefUtils.SF_KEY_FIREBASE_TOKEN, s)
    }

    override fun onMessageReceived(@NotNull remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.let {
            Log.d("Notification_title", it.notification?.title.toString())
            Log.d("Notification_body", it.notification?.body.toString())
            Log.d("Notification_click", it.notification?.clickAction.toString())
            Log.d("Notification_data", it.data.toString())

            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, createNotificationChannel())
                    .setContentTitle(it.notification?.title)
                    .setContentText(it.notification?.body)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setStyle(NotificationCompat.BigTextStyle())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val contentIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, PushNotificationView::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            notificationBuilder.setContentIntent(contentIntent)
            notificationManager.notify(4848, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel(): String {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "bootstrap"
            val name: CharSequence = "BootStrap"
            var description = "Bootstrap notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = description
                setShowBadge(true)
            }
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = applicationContext.getSystemService(
                NotificationManager::class.java
            )!!
            notificationManager.createNotificationChannel(channel)
            channel.id
        } else {
            ""
        }
    }

}