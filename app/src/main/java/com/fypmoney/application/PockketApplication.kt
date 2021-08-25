package com.fypmoney.application

import android.app.Application
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerLibCore
import com.vanniktech.emoji.google.GoogleEmojiProvider

import com.vanniktech.emoji.EmojiManager


/**
 * Application class to get the context from anywhere in the application
 */

class PockketApplication : Application() {
    private val AF_DEV_KEY = "xLiRBq3f8fimR7F9zbzzcE"
    companion object {
        lateinit var instance: PockketApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        EmojiManager.install(GoogleEmojiProvider())
        val conversionListener: AppsFlyerConversionListener =
            object : AppsFlyerConversionListener {
                /* Returns the attribution data. Note - the same conversion data is returned every time per install */
                override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                    for (attrName in conversionData.keys) {
                        Log.d(
                            AppsFlyerLibCore.LOG_TAG,
                            "attribute: " + attrName + " = " + conversionData[attrName]
                        )
                    }
                    setInstallData(conversionData)
                }

                override fun onConversionDataFail(errorMessage: String) {
                    Log.d(AppsFlyerLibCore.LOG_TAG, "error getting conversion data: $errorMessage")
                }

                /* Called only when a Deep Link is opened */
                override fun onAppOpenAttribution(conversionData: Map<String, String>) {
                    for (attrName in conversionData.keys) {
                        Log.d(
                            AppsFlyerLibCore.LOG_TAG,
                            "attribute: " + attrName + " = " + conversionData[attrName]
                        )
                    }
                }

                override fun onAttributionFailure(errorMessage: String) {
                    Log.d(AppsFlyerLibCore.LOG_TAG, "error onAttributionFailure : $errorMessage")
                }
            }


        /* This API enables AppsFlyer to detect installations, sessions, and updates. */


        /* This API enables AppsFlyer to detect installations, sessions, and updates. */
        AppsFlyerLib.getInstance()
            .init(
                AF_DEV_KEY, conversionListener,
                applicationContext
            )
        AppsFlyerLib.getInstance().startTracking(this)


        /* Set to true to see the debug logs. Comment out or set to false to stop the function */


        /* Set to true to see the debug logs. Comment out or set to false to stop the function */AppsFlyerLib.getInstance()
            .setDebugLog(true)
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