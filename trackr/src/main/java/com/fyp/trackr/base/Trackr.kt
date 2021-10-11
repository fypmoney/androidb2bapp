package com.fyp.trackr.base

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.fyp.trackr.models.AnalyticsEvent
import com.fyp.trackr.models.ScreenEvent
import com.fyp.trackr.services.TrackrServices
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

object Trackr {
    private const val TAG = "Trackr"
    private const val COUNTER_KEY = "ct_event_counter_key"
    private var fire: FirebaseAnalytics? = null
    private var loglevel = LogLevel.PROD
    private var app: Application? = null
    var activity: FragmentActivity? = null

    private var sharedPreference: SharedPreferences? = null
    private var mainHandler: Handler? = null

    fun setLogLevel(logLevel: LogLevel) {
        this.loglevel = logLevel
    }

    fun register(app: Application) {
        this.app = app
    }
    fun initialize(app: Application ) {
        if (fire == null)
            fire = FirebaseAnalytics.getInstance(app.applicationContext)

        GlobalScope.launch {
            var adInfo: AdvertisingIdClient.Info? = null
            var andiInfo: String? = null
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(app.applicationContext)
                andiInfo = Settings.Secure.getString(app.contentResolver, Settings.Secure.ANDROID_ID)
            } catch (exception: IOException) {
                exception.printStackTrace()
            } catch (exception: GooglePlayServicesNotAvailableException) {
                exception.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mainHandler = Handler(app.mainLooper)
        initSharedPreference(app)
        addNotificationChannels(app)
    }

    private fun initSharedPreference(app: Application) {
        sharedPreference = app.getSharedPreferences("ct_counter_pref", Context.MODE_PRIVATE)
    }

    fun addNotificationChannels(app: Application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            /*CleverTapAPI.createNotificationChannel(app,
                "news",
                "News",
                "News",
                NotificationManager.IMPORTANCE_HIGH,
                true)
            CleverTapAPI.createNotificationChannel(app,
                "updates",
                "Updates",
                "Updates",
                NotificationManager.IMPORTANCE_HIGH,
                true)
            CleverTapAPI.createNotificationChannel(app,
                "misc",
                "Miscellaneous",
                "Miscellaneous",
                NotificationManager.IMPORTANCE_HIGH,
                true)*/
        }
    }

    fun login(userId: String) {
        fire?.setUserId(userId)
        updateUserId(userId)
    }
    fun login(data: HashMap<String, Any>) {

    }

    fun data(data: HashMap<String, Any>) {
        for (data_point in data) {
            fire?.setUserProperty(data_point.key, data_point.value.toString())
        }
    }
    fun updateUserId(userId: String?){
        sharedPreference?.edit()?.putString("user_id", userId)?.apply()
    }
    fun location(location: Location?) {
    }

    fun a(event: AnalyticsEvent?) {
        if (event == null) return
        if (loglevel.num <= LogLevel.ANALYTICS.num) {
            mainHandler?.post {
                try {
                    showEventToast(event)
                } catch (e: ConcurrentModificationException) {
                    Log.e(TAG, "a", e)
                }
            }
        }
        val eventCount = incrementEventCount()
        for (service in event.services) {
            when (service) {
                TrackrServices.MOENGAGE -> {
                    if (loglevel.num <= LogLevel.DEBUG.num) {
                        Log.d("CT_EVENT", "Data: $event")
                    }
                    event.data()["counter"] = eventCount
                    /*if (event.data().isEmpty())
                        clevertap?.pushEvent(event.name.name)
                    else
                        clevertap?.pushEvent(event.name.name, event.data())*/
                }
                TrackrServices.FIREBASE -> {
                    if (loglevel.num <= LogLevel.DEBUG.num) {
                        Log.d("FIRE_EVENT", "Data: $event")
                    }
                    if (event.data().isEmpty())
                        fire?.logEvent(event.name.name, Bundle.EMPTY)
                    else
                        fire?.logEvent(event.name.name, event.bundle())
                }
            }
        }
    }

    private fun showEventToast(event: AnalyticsEvent) {
        val eventData = event.data()
        Toast.makeText(
            app?.applicationContext,
            "${event.name.name} at ${event.services} with $eventData",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun incrementEventCount(): Int {
        val incrementCount = getEventCount() + 1
        sharedPreference?.edit()
            ?.putInt(COUNTER_KEY, incrementCount)
            ?.apply()
        return incrementCount
    }

    private fun getEventCount(): Int {
        return sharedPreference?.getInt(COUNTER_KEY, 0) ?: 0
    }

    fun s(event: ScreenEvent?) {
        if (event == null) return
        for (service in event.services) {
            when (service) {
                TrackrServices.MOENGAGE -> {

                }
                TrackrServices.FIREBASE -> {

                }
            }
        }
    }

    enum class LogLevel(val num: Int) {
        DEBUG(0), INFO(1), PROD(2), ANALYTICS(-1)
    }
}