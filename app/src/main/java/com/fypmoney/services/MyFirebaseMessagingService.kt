package com.fypmoney.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fypmoney.R
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.activity.NotificationView
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
            /*Log.d("Notification_title", it.notification?.title.toString())
            Log.d("Notification_body", it.notification?.body.toString())
            Log.d("Notification_image", it.notification?.imageUrl.toString())
            Log.d("Notification_click", it.notification?.clickAction.toString())
            Log.d("Notification_data", it.data.toString())
            Log.d("sjghe8ts9ge_notific",it.notification.toString())*/
            Log.d("sjghe8ts9ge_data",it.data.toString())


            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, createNotificationChannel())
                    .setContentTitle("testing title")
                    .setContentText("testing body")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setStyle(NotificationCompat.BigTextStyle())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

           /* val intent = Intent(this,  Class.forName(AppConstants.BASE_ACTIVITY_URL + it.data.get().getStringExtra("tag")))
            intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)

            if (intent.hasExtra("tag")) {
                try {
                    intentToActivity(
                        Class.forName(AppConstants.BASE_ACTIVITY_URL + intent.getStringExtra("tag")),intent.getStringExtra("type")
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    intentToActivity(HomeView::class.java)
                }
                finish()
            }
            */
            val contentIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, NotificationView::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            notificationBuilder.setContentIntent(contentIntent)
            notificationManager.notify(4848, notificationBuilder.build())
            val uiHandler = Handler(Looper.getMainLooper())
            uiHandler.post(Runnable {
                Glide.with(this)
                    .asBitmap()
                    .load(it.notification?.imageUrl)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            notificationBuilder.setLargeIcon(resource)
                            notificationManager.notify(4848, notificationBuilder.build())
                        }
                    })
            })
        }
    }

    private fun createNotificationChannel(): String {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "fypmoney"
            val name: CharSequence = "FypMoney"
            var description = "Fypmoney notification"
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