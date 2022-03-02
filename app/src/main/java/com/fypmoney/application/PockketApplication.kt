package com.fypmoney.application

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatConfig
import com.freshchat.consumer.sdk.FreshchatNotificationConfig
import com.fyp.trackr.base.Trackr
import com.fypmoney.BuildConfig
import com.fypmoney.R
import com.fypmoney.model.KeysFound
import com.fypmoney.notification.NotificationUtils
import com.fypmoney.notification.NotificationUtils.FESTIVAL_PROMOTIONAL_CHANNEL_ID
import com.fypmoney.notification.NotificationUtils.PROMOTIONAL_CHANNEL_ID
import com.fypmoney.notification.NotificationUtils.TRANSACTION_CHANNEL_ID
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moe.pushlibrary.MoEHelper
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider


/**
 * Application class to get the context from anywhere in the application
 */

class PockketApplication : Application() {

    var appUpdateRequired: Boolean = false
     var freshchat: Freshchat? = null

    companion object {
        lateinit var instance: PockketApplication
        var homeScreenErrorMsg: String? = null
        var isNewFeedAvailableData: KeysFound? = null
        var isLoadMoneyPopupIsShown = false

    }

    override fun onCreate() {
        // register trackr
        Trackr.register(this)
        super.onCreate()
        instance = this
        EmojiManager.install(GoogleEmojiProvider())

        initialiseFreshchat()

        //Check User is logged in or not.
        if(SharedPrefUtils.getBoolean(
                this,
                SharedPrefUtils.SF_KEY_IS_LOGIN
            )!!){
            setUserForCrashReports(this)
        }

        NotificationUtils.createNotificationChannel(applicationContext=this,
            channelId = TRANSACTION_CHANNEL_ID,
            channelName = "FYP Transaction",
            channelDescription = "Notification channel related to transaction notification",
            notificationImportance = NotificationManager.IMPORTANCE_DEFAULT
        )
        NotificationUtils.createNotificationChannel(applicationContext=this,
            channelId = PROMOTIONAL_CHANNEL_ID,
            channelName = "FYP Promotional",
            channelDescription = "Promotional Notification",
            notificationImportance = NotificationManager.IMPORTANCE_DEFAULT
        )
        NotificationUtils.createNotificationChannel(
            applicationContext = this,
            channelId = FESTIVAL_PROMOTIONAL_CHANNEL_ID,
            channelName = "Festival  Promotional",
            channelDescription = "Festival Notification",
            notificationImportance = NotificationManager.IMPORTANCE_DEFAULT
        )
        // init analytics

        // init analytics
        Trackr.setLogLevel(if (BuildConfig.DEBUG) Trackr.LogLevel.ANALYTICS else Trackr.LogLevel.PROD)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Trackr.initialize(
                this,
                BuildConfig.ADJUST_PROD_KEY,
                BuildConfig.MOENAGE_KEY,
                R.drawable.ic_notification,
                R.mipmap.ic_launcher_round,
                R.color.colorPrimary
            )
        } else {
            Trackr.initialize(
                this,
                BuildConfig.ADJUST_PROD_KEY,
                BuildConfig.MOENAGE_KEY,
                R.drawable.ic_notification_png,
                R.mipmap.ic_launcher_foreground,
                R.color.colorPrimary
            )

        }


        val contextList = arrayListOf("C1", "C2", "C3", "C4")
        MoEHelper.getInstance(this).appContext = contextList
    }


    private fun setUserForCrashReports(context: Context) {
        try {
            FirebaseCrashlytics.getInstance()
                .setUserId(SharedPrefUtils.getLong(
                    context, key = SharedPrefUtils.SF_KEY_USER_ID).toString())
            FirebaseCrashlytics.getInstance()
                .setCustomKey("user_id", SharedPrefUtils.getLong(
                    context, key = SharedPrefUtils.SF_KEY_USER_ID).toString())
            SharedPrefUtils.getString(
                context, key = SharedPrefUtils.SF_KEY_USER_MOBILE)?.let {
                FirebaseCrashlytics.getInstance()
                    .setCustomKey("phone", it)
            }
            SharedPrefUtils.getString(
                context, key = SharedPrefUtils.SF_KEY_USER_FIRST_NAME)?.let {
                FirebaseCrashlytics.getInstance()
                    .setCustomKey("full_name", "$it ${SharedPrefUtils.getString(
                        context, key = SharedPrefUtils.SF_KEY_USER_LAST_NAME)}")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun initialiseFreshchat() {
        val config = FreshchatConfig(
            AppConstants.FRESH_CHAT_APP_ID,
            AppConstants.FRESH_CHAT_APP_KEY
        )
        config.domain = AppConstants.FRESH_CHAT_DOMAIN
        config.isCameraCaptureEnabled = true
        config.isGallerySelectionEnabled = true
        config.isResponseExpectationEnabled = true
        config.isTeamMemberInfoVisible = true

        config.isUserEventsTrackingEnabled = true

        val freshChat = getFreshchatInstance(applicationContext)
        val user = freshChat.user.apply {
            firstName = SharedPrefUtils.getString(
                this@PockketApplication,
                SharedPrefUtils.SF_KEY_USER_FIRST_NAME
            )
            lastName = SharedPrefUtils.getString(
                this@PockketApplication,
                SharedPrefUtils.SF_KEY_USER_LAST_NAME
            )

        }
        user.setPhone(
            "+91", SharedPrefUtils.getString(
                this@PockketApplication,
                SharedPrefUtils.SF_KEY_USER_MOBILE
            )

        )
        freshChat.user = user
        freshChat.init(config)
        val soundUri =
            Uri.parse("android.resource://" + applicationContext.packageName + "/" + R.raw.notification_sound)
        var notificationConfig: FreshchatNotificationConfig? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationConfig = FreshchatNotificationConfig().apply {
                smallIcon = R.drawable.ic_notification
                largeIcon = R.mipmap.ic_launcher_round
                isNotificationSoundEnabled = true
                notificationSound = soundUri
                importance = NotificationManagerCompat.IMPORTANCE_MAX
            }
        } else {
            notificationConfig = FreshchatNotificationConfig().apply {
                smallIcon = R.drawable.ic_notification_png
                largeIcon = R.mipmap.ic_launcher_foreground
                isNotificationSoundEnabled = true
                notificationSound = soundUri
                importance = NotificationManagerCompat.IMPORTANCE_MAX
            }
        }

        freshChat.setNotificationConfig(notificationConfig)
    }

    private fun getFreshchatInstance(context: Context): Freshchat {
        if (freshchat == null) {
            freshchat = Freshchat.getInstance(context)
        }
        return freshchat!!
    }

}