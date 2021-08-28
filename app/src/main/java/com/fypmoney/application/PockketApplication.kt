package com.fypmoney.application

import android.app.Application
import android.util.Log

import com.vanniktech.emoji.google.GoogleEmojiProvider

import com.vanniktech.emoji.EmojiManager
import com.adjust.sdk.Adjust

import com.adjust.sdk.AdjustConfig
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle


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
        super.onCreate()
        instance = this
        EmojiManager.install(GoogleEmojiProvider())
        val appToken = "buqdhv6bqlts"
        val environment = AdjustConfig.ENVIRONMENT_PRODUCTION
        val config = AdjustConfig(this, appToken, environment)
        Adjust.onCreate(config)
        registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())


    }
    private class AdjustLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
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

}