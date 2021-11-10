package com.fypmoney.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import com.fypmoney.BuildConfig
import com.fypmoney.R

object NotificationUtils {
     private val TAG = NotificationUtils::class.java.simpleName
     const val TRANSACTION_CHANNEL_ID = "fyp_transaction_channel"
     const val PROMOTIONAL_CHANNEL_ID = "fyp_promotional_channel"
     const val FESTIVAL_PROMOTIONAL_CHANNEL_ID = "fyp_festival_promotional_channel"
     const val GENRAL_CHANNEL_ID = "fyp_general_channel"
     const val RICH_CONTENT_CHANNEL_ID = "moe_rich_content"

    fun createNotificationChannel(applicationContext:Application,
                                          channelId:String,
                                          channelName:String,
                                          channelDescription:String,
                                          notificationImportance:Int=NotificationManager.IMPORTANCE_DEFAULT){

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val channel = NotificationChannel(channelId, channelName, notificationImportance).apply {
                 description = channelDescription
                 setShowBadge(true)
             }
             val audioAttributes = AudioAttributes.Builder()
                 .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                 .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                 .build()
             if(channel.id==PROMOTIONAL_CHANNEL_ID){
                 channel.setSound(
                     Uri.parse(
                         ContentResolver.SCHEME_ANDROID_RESOURCE
                                 + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.notification_sound), audioAttributes)
             }
             if(channel.id==FESTIVAL_PROMOTIONAL_CHANNEL_ID){
                 channel.setSound(
                     Uri.parse(
                         ContentResolver.SCHEME_ANDROID_RESOURCE
                                 + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.diwali_push_sound), audioAttributes)
             }else{
                 channel.setSound(
                     Uri.parse(
                         ContentResolver.SCHEME_ANDROID_RESOURCE
                                 + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.notification_sound), audioAttributes)
             }

             val notificationManager = applicationContext.getSystemService(
                 NotificationManager::class.java
             )!!
             notificationManager.createNotificationChannel(channel)
            Log.d(TAG,"notification channel created with id: $channelId")
        }
    }

    fun getChannelId(channelName:Channels):String{
        return when(channelName){
            Channels.Promotional -> {
                PROMOTIONAL_CHANNEL_ID
            }
            Channels.Transaction -> {
                TRANSACTION_CHANNEL_ID
            }
            Channels.General -> {
                GENRAL_CHANNEL_ID
            }
            Channels.FESTIVALPROMOTIONAL -> {
                FESTIVAL_PROMOTIONAL_CHANNEL_ID
            }
        }
    }
    sealed class Channels{
        object Transaction:Channels()
        object Promotional:Channels()
        object FESTIVALPROMOTIONAL:Channels()
        object General:Channels()
    }

}