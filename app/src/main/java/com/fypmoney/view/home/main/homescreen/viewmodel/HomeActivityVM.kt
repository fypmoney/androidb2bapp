package com.fypmoney.view.home.main.homescreen.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.UserDeviceInfo
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import java.util.*


class HomeActivityVM(application: Application): BaseViewModel(application) {
    private val TAG = HomeActivityVM::class.java.simpleName
    val isUnreadNotificationAvailable = SharedPrefUtils.getString(
        application,
        SharedPrefUtils.SF_KEY_NEW_MESSAGE
    )
    val userProfileUrl = SharedPrefUtils.getString(
        application,
        SharedPrefUtils.SF_KEY_PROFILE_IMAGE
    )



    var toolbarTitle = MutableLiveData(
        "Hey ${Utility.getCustomerDataFromPreference()?.firstName},")

    val event:LiveData<HomeActivityEvent>
        get() = _event
    private var _event = LiveEvent<HomeActivityEvent>()

    fun onProfileClicked() {
        _event.value = HomeActivityEvent.ProfileClicked
    }

    fun onNotificationClicked() {
        _event.value = HomeActivityEvent.NotificationClicked
    }

    override fun onCleared() {
        super.onCleared()
        storeFirstTimeUserLandedOnHomeScreen()
    }

    private fun storeFirstTimeUserLandedOnHomeScreen() {
        SharedPrefUtils.getLong(
            PockketApplication.instance,
            SharedPrefUtils.SF_IS_USER_LANDED_ON_HOME_SCREEN_TIME
        ).let {
            if (it == 0L) {
                SharedPrefUtils.putLong(
                    PockketApplication.instance,
                    SharedPrefUtils.SF_IS_USER_LANDED_ON_HOME_SCREEN_TIME, Date().time
                )
            }
        }
    }

    fun postLatlong(latitude: String, longitude: String, userId: Long) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_USER_DEVICE_INFO,
                endpoint = NetworkUtil.endURL(ApiConstant.API_USER_DEVICE_INFO),
                request_type = ApiUrl.PUT,
                param = UserDeviceInfo(
                    latitude = latitude,
                    longitude = longitude,
                    userId = userId,
                    make = Build.BRAND,
                    model = Build.MODEL,
                    modelVersion = Build.ID,
                    timezone = TimeZone.getDefault().getDisplayName(
                        Locale.ROOT
                    ),
                    locale = PockketApplication.instance.resources.configuration.locale.country,
                    dtoken = SharedPrefUtils.getString(
                        PockketApplication.instance,
                        SharedPrefUtils.SF_KEY_FIREBASE_TOKEN
                    ) ?: "",
                    isHomeViewed = "YES",
                    rfu1 = getAllInstalledApps()

                ), onResponse = this,
                isProgressBar = false
            )
        )
    }


    private fun getAllInstalledApps():String?{
        return if(!checkListOfAppIsSynced()){
            val pm: PackageManager = PockketApplication.instance.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            SharedPrefUtils.putBoolean(
                PockketApplication.instance,
                SharedPrefUtils.SF_IS_INSTALLED_APPS_SYNCED,
                true
            )
            packages.map { it.name }.toString()
        }else{
            null
        }


    }
    private fun checkListOfAppIsSynced():Boolean{
        return SharedPrefUtils.getBoolean(
            PockketApplication.instance,
            SharedPrefUtils.SF_IS_INSTALLED_APPS_SYNCED
        ) ?: false
    }

    sealed class HomeActivityEvent {
        object ProfileClicked : HomeActivityEvent()
        object NotificationClicked : HomeActivityEvent()
    }
}