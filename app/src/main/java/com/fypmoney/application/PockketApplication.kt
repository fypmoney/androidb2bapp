package com.fypmoney.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.fyp.trackr.base.Trackr
import com.fypmoney.BuildConfig
import com.fypmoney.util.SharedPrefUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider


/**
 * Application class to get the context from anywhere in the application
 */

class PockketApplication : Application() {
    var appUpdateRequired:Boolean = false
    companion object {
        lateinit var instance: PockketApplication
            private set
    }

    override fun onCreate() {
        // register trackr
        Trackr.register(this)
        super.onCreate()
        instance = this
        EmojiManager.install(GoogleEmojiProvider())

        //Check User is logged in or not.
        if(SharedPrefUtils.getBoolean(
                this,
                SharedPrefUtils.SF_KEY_IS_LOGIN
            )!!){
            setUserForCrashReports(this)
        }

        // init analytics

        // init analytics
        Trackr.setLogLevel(if (BuildConfig.DEBUG) Trackr.LogLevel.ANALYTICS else Trackr.LogLevel.PROD)
        Trackr.initialize(
            this,
            BuildConfig.ADJUST_PROD_KEY,
            BuildConfig.MOENAGE_KEY
        )

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
}