package com.fyp.trackr.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.facebook.appevents.AppEventsLogger
import com.fyp.trackr.BuildConfig
import com.fyp.trackr.SERVER_DATE_TIME_FORMAT1
import com.fyp.trackr.models.AnalyticsEvent
import com.fyp.trackr.models.ScreenEvent
import com.fyp.trackr.models.TrackrEvent
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
    private var adjust: Adjust? = null
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
        this.app!!.registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())
    }
    fun initialize(app: Application, adjustKey:String = "",
                   moEngageKey:String = "",notiSmallIcon:Int = 0,notiLargeIcon:Int = 0,notiColor:Int = 0 ) {
        if (fire == null)
            fire = FirebaseAnalytics.getInstance(app.applicationContext)

        if(adjust==null){
            val appToken = adjustKey
            val environment = AdjustConfig.ENVIRONMENT_PRODUCTION
            val config = AdjustConfig(app, appToken, environment)
            config.setUrlStrategy("URL_STRATEGY_INDIA")
            Adjust.onCreate(config)
        }
        if(moEngage==null){
            if(BuildConfig.DEBUG){
                moEngage = MoEngage.Builder(app, moEngageKey)
                    .configureLogs(LogConfig(VERBOSE, false))
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
                    .configureLogs(LogConfig(VERBOSE, false) )
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
                TrackrServices.ADJUST -> {
                    if (loglevel.num <= LogLevel.DEBUG.num) {
                        Log.d("ADJUST_EVENT", "Data: $event")
                    }
                    var adjustEventName = ""
                    when(event.name.name){
                        TrackrEvent.account_creation.name->{
                            adjustEventName = "tnpgjs"
                        }
                        TrackrEvent.kyc_verification.name->{
                            adjustEventName = "eesdlf"
                        }
                        TrackrEvent.kyc_verification_teen.name->{
                            adjustEventName = "p7vxsb"
                        }
                        TrackrEvent.kyc_verification_adult.name->{
                            adjustEventName = "wcb2ih"
                        }
                        TrackrEvent.kyc_verification_other.name->{
                            adjustEventName = "6shjhs"
                        }
                        TrackrEvent.load_money_fail.name->{
                            adjustEventName = "1x4oan"
                        }
                        TrackrEvent.load_money_success.name->{
                            adjustEventName = "gerdxc"
                        }
                        TrackrEvent.mission_given_success.name->{
                            adjustEventName = "fwz8lp"
                        }
                        TrackrEvent.add_familymember.name->{
                            adjustEventName = "of1zqz"
                        }
                        TrackrEvent.order_success.name->{
                            adjustEventName = "5s49wf"
                        }
                        TrackrEvent.refferal_shared.name->{
                            adjustEventName = "4nxchn"
                        }
                    }
                    Log.d("ADJUST_EVENT_KEY", "KEY: $adjustEventName")
                    Adjust.trackEvent(AdjustEvent(adjustEventName))

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
            }
        }
    }

    enum class LogLevel(val num: Int) {
        DEBUG(0), INFO(1), PROD(2), ANALYTICS(-1)
    }

    private class AdjustLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
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
            app?.applicationContext?.let { MoEHelper.getInstance(it).resetAppContext() }

        }
    }

}