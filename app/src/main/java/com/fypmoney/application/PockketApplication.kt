package com.fypmoney.application

import android.app.Application
import android.util.Log

import com.vanniktech.emoji.google.GoogleEmojiProvider

import com.vanniktech.emoji.EmojiManager


/**
 * Application class to get the context from anywhere in the application
 */

class PockketApplication : Application() {
    private val AF_DEV_KEY = "xLiRBq3f8fimR7F9zbzzcE"
    var appUpdateRequired:Boolean = false
    companion object {
        lateinit var instance: PockketApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        EmojiManager.install(GoogleEmojiProvider())




    }

    var InstallConversionData = ""
    var sessionCount = 0
    fun setInstallData(conversionData: Map<String, Any>) {
        if (sessionCount == 0) {
            val install_type = """
            Install Type: ${conversionData["af_status"]}
            
            """.trimIndent()
            val media_source = """
            Media Source: ${conversionData["media_source"]}
            
            """.trimIndent()
            val install_time = """
            Install Time(GMT): ${conversionData["install_time"]}
            
            """.trimIndent()
            val click_time = """
            Click Time(GMT): ${conversionData["click_time"]}
            
            """.trimIndent()
            val is_first_launch = """
            Is First Launch: ${conversionData["is_first_launch"]}
            
            """.trimIndent()
            InstallConversionData += install_type + media_source + install_time + click_time + is_first_launch
            sessionCount++
        }
    }
}