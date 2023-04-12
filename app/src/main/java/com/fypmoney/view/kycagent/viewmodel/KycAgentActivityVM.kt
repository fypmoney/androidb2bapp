package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.UserDeviceInfo
import com.fypmoney.util.SharedPrefUtils
import kotlinx.coroutines.launch
import java.util.*

class KycAgentActivityVM(application: Application) : BaseViewModel(application) {

    fun postLatlong(latitude: String, longitude: String, userId: Long) {
        Log.d("KycAgent","viewmodelscope: ${Thread.currentThread().name}")
        viewModelScope.launch {
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
                        rfu1 = ""

                    ), onResponse = this@KycAgentActivityVM,
                    isProgressBar = false
                )
            )
        }
        Log.d("KycAgent","running thread name in out of viewmodelscope: ${Thread.currentThread().name}")

    }

}