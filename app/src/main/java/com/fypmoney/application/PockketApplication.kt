package com.fypmoney.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.fypmoney.util.SharedPrefUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moengage.core.DataCenter
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import com.moengage.core.MoEngage


/**
 * Application class to get the context from anywhere in the application
 */

class PockketApplication : Application() {
    var appUpdateRequired: Boolean = false

    companion object {
        lateinit var instance: PockketApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        val moEngage = MoEngage.Builder(this, "E5IA56TQXEJ5P7B1CULT4CCE")
            .setDataCenter(DataCenter.DATA_CENTER_3)

            .build()
        MoEngage.initialise(moEngage)
        instance = this
        EmojiManager.install(GoogleEmojiProvider())
        val appToken = "buqdhv6bqlts"
        val environment = AdjustConfig.ENVIRONMENT_PRODUCTION
        val config = AdjustConfig(this, appToken, environment)
        Adjust.onCreate(config)
        registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())
        //var appSignature = AppSignatureHelper(this)
        //appSignature.appSignatures

        //Check User is logged in or not.
        if (SharedPrefUtils.getBoolean(
                this,
                SharedPrefUtils.SF_KEY_IS_LOGIN
            )!!
        ) {
            setUserForCrashReports(this)
        }


//        val analytics = Analytics.Builder(this, "M7jjorCvQXP6MGtnekIgreVCY5hR9Aae")
//            .trackApplicationLifecycleEvents() // Enable this to record certain application events automatically!
//            .recordScreenViews() // Enable this to record screen views automatically!
//            .build()
//
//// Set the initialized instance as a globally accessible instance.
//        Analytics.setSingletonInstance(analytics);


    }
    private class AdjustLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            Adjust.onResume()
        }

        override fun onActivityPaused(activity: Activity) {
            Adjust.onPause()
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }
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