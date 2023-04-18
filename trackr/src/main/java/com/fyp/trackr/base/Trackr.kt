package com.fyp.trackr.base

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.facebook.appevents.AppEventsLogger
import com.fyp.trackr.BuildConfig
import com.fyp.trackr.SERVER_DATE_TIME_FORMAT1
import com.fyp.trackr.models.AnalyticsEvent
import com.fyp.trackr.models.ScreenEvent
import com.fyp.trackr.parseDateStringIntoDate
import com.fyp.trackr.services.TrackrServices
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.DataCenter
import com.moengage.core.LogLevel.VERBOSE
import com.moengage.core.MoEngage
import com.moengage.core.Properties
import com.moengage.core.config.FcmConfig
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
import com.moengage.core.internal.MoEConstants.USER_ATTRIBUTE_USER_BDAY
import com.moengage.core.model.AppStatus

object Trackr {
    private const val TAG = "Trackr"
    private const val COUNTER_KEY = "ct_event_counter_key"
    private var fire: FirebaseAnalytics? = null
    private var moEngage: MoEngage? = null
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
    fun initialize(app: Application,
                   moEngageKey:String = "",notiSmallIcon:Int = 0,notiLargeIcon:Int = 0,notiColor:Int = 0 ) {
        if (fire == null)
            fire = FirebaseAnalytics.getInstance(app.applicationContext)


        if(moEngage==null){
            if(BuildConfig.DEBUG){
                moEngage = MoEngage.Builder(app, moEngageKey)
                    .configureLogs(LogConfig(VERBOSE, true))
                    .setDataCenter(DataCenter.DATA_CENTER_3)
                    .configureNotificationMetaData(
                        NotificationConfig(notiSmallIcon,
                        notiLargeIcon,
                        notiColor, "notification_sound",
                            true,
                            isBuildingBackStackEnabled = false, isLargeIconDisplayEnabled = true)
                    )
                    .configureFcm(FcmConfig(false))
                    .build()
                MoEngage.initialise(moEngage!!)
            }else{
                moEngage = MoEngage.Builder(app, moEngageKey)
                    .setDataCenter(DataCenter.DATA_CENTER_3)
                    .configureLogs(LogConfig(VERBOSE, true) )
                    .configureNotificationMetaData(
                        NotificationConfig(
                            notiSmallIcon,
                            notiLargeIcon,
                            notiColor, "notification_sound",
                            true,
                            isBuildingBackStackEnabled = true, isLargeIconDisplayEnabled = true)
                    )
                    .configureFcm(FcmConfig(false))
                    .build()
                MoEngage.initialise(moEngage!!)
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
        app?.let { MoEHelper.getInstance(it).setUniqueId(userId) }
        updateUserId(userId)
    }
    fun login(data: HashMap<String, Any>) {
    }



    fun logOut(){
        MoEHelper.getInstance(app!!).logoutUser()
    }

    fun data(data: HashMap<String, Any>) {
        for (data_point in data) {
            fire?.setUserProperty(data_point.key, data_point.value.toString())
        }
        MoEHelper.getInstance(app!!).setUserAttribute(data)

    }
    fun sendDob(dateOfBirth:String?){
        val dob = parseDateStringIntoDate(dateOfBirth,
            inputFormat = SERVER_DATE_TIME_FORMAT1)
        dob?.let { it1 ->
            app?.applicationContext?.let {
                MoEHelper.getInstance(it).setUserAttribute(USER_ATTRIBUTE_USER_BDAY,it1)
            }
        }
    }
    fun updateUserId(userId: String?){
        sharedPreference?.edit()?.putString("user_id", userId)?.apply()
    }
    fun location(location: Location?) {
    }

    fun appIsInstallFirst(isFirstTime:Boolean){
        if(isFirstTime){
            app?.let { MoEHelper.getInstance(it).setAppStatus(AppStatus.INSTALL) }
        }else{
            app?.let { MoEHelper.getInstance(it).setAppStatus(AppStatus.UPDATE) }
        }
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
                        Log.d("MOENGAGE_EVENT", "Data: $event")
                    }
                    event.data()["counter"] = eventCount
                    val properties = Properties()
                    if(event.data().isEmpty()){
                        MoEHelper.getInstance(app!!).trackEvent(event.name.name, properties)

                    }else{
                        for (property in event.data()){
                            properties.addAttribute(property.key,property.value)
                        }
                        MoEHelper.getInstance(app!!).trackEvent(event.name.name, properties)
                    }
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
                TrackrServices.FB -> {
                    try{
                        app?.applicationContext?.let { AppEventsLogger.newLogger(it).logEvent(event.name.name) }
                    }catch (e:Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)
                        FirebaseCrashlytics.getInstance().setCustomKey("event_name",event.name.name)
                    }

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
                else -> {}
            }
        }
    }

    enum class LogLevel(val num: Int) {
        DEBUG(0), INFO(1), PROD(2), ANALYTICS(-1)
    }


}