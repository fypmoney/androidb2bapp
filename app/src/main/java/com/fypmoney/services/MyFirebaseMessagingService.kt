package com.fypmoney.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.adjust.sdk.Adjust
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fypmoney.BuildConfig
import com.fypmoney.R
import com.fypmoney.notification.NotificationUtils
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.activity.HomeView
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moengage.firebase.MoEFireBaseHelper
import com.moengage.pushbase.MoEPushHelper
import org.jetbrains.annotations.NotNull


/*
*  This class is used to handle the push notifications in the application
* */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("FCMToken", s)
        MoEFireBaseHelper.getInstance().passPushToken(applicationContext,s)
        Adjust.setPushToken(s, applicationContext);

        SharedPrefUtils.putString(applicationContext, SharedPrefUtils.SF_KEY_FIREBASE_TOKEN, s)

    }

    override fun onMessageReceived(@NotNull remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCMToken", remoteMessage.data.toString())

        val result = remoteMessage.data
        SharedPrefUtils.putString(applicationContext, SharedPrefUtils.SF_KEY_NEW_MESSAGE, "new")

        if (MoEPushHelper.getInstance().isFromMoEngagePlatform(remoteMessage.data)){
            MoEFireBaseHelper.getInstance().passPushPayload(applicationContext, remoteMessage.data)
        }else{
            // your app's business logic to show notification
            val res = remoteMessage.data["notificationType"]
            Log.d("FCMToken_data", result.toString())



            remoteMessage.let {
                val notificationBuilder: NotificationCompat.Builder =
                    NotificationCompat.Builder(this, NotificationUtils.getChannelId(
                        NotificationUtils.Channels.General
                    ))
                        .setContentTitle(it.notification?.title.toString())
                        .setContentText(it.notification?.body.toString())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setStyle(NotificationCompat.BigTextStyle())
                        .setSmallIcon(R.drawable.ic_notification)
                        .setSound(Uri.parse(
                            ContentResolver.SCHEME_ANDROID_RESOURCE
                                    + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.notification_sound))
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                        .setTicker(resources.getString(R.string.app_name))
                        .setAutoCancel(true)

                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


                val contentIntent = PendingIntent.getActivity(
                    this,
                    0,
                    onNotificationClick(remoteMessage),
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

    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>, type: String? = null, aprid: String? = null) {
        val intent = Intent(applicationContext, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.NOTIFICATION_APRID, aprid)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

    }

    private fun onNotificationClick(remoteMessage: RemoteMessage): Intent {
        when (remoteMessage.data[AppConstants.NOTIFICATION_KEY_NOTIFICATION_TYPE]) {
            AppConstants.NOTIFICATION_TYPE_IN_APP_DIRECT -> {
                Log.d("chacknoticlicked", "t")
                when (remoteMessage.data[AppConstants.NOTIFICATION_KEY_TYPE]) {


                    AppConstants.TYPE_APP_SLIDER_NOTIFICATION -> {
                        val intent = Intent(applicationContext, HomeView::class.java)
                        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.NOTIFICATION)
                        intent.putExtra(
                            AppConstants.NOTIFICATION_APRID,
                            remoteMessage.data[AppConstants.NOTIFICATION_KEY_APRID]
                        )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP

                        return intent

                    }
                    AppConstants.TYPE_NONE_NOTIFICATION -> {
                        Log.d("chacknoticlicked2", "t")
                        try {
                            val intent = Intent(
                                applicationContext,
                                Class.forName(AppConstants.BASE_ACTIVITY_URL + remoteMessage.data["url"])
                            )
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            return intent

                        } catch (e: Exception) {
                            e.printStackTrace()
                            intentToActivity(HomeView::class.java)
                        }

                    }
                }

            }
        }
        return Intent()
    }


}