package com.fypmoney.application

import android.app.Application


/**
 * Application class to get the context from anywhere in the application
 */

class PockketApplication : Application() {
    companion object {
        lateinit var instance: PockketApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}