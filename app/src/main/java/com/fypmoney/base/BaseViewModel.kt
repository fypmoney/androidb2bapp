package com.fypmoney.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.AppDatabase
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 *Base View Model class
 */

abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    WebApiCaller.OnWebApiResponse {
    var progressDialog = MutableLiveData(false)
    val internetError:LiveEvent<Boolean> = LiveEvent()
    val logoutUser:LiveEvent<Boolean> = LiveEvent()
    var appDatabase: AppDatabase? = null

    init {
        appDatabase = AppDatabase.getInstance()
    }

    override fun onSuccess(purpose: String, responseData: Any) {
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        if(errorResponseInfo.errorCode == "401"){
            logoutUser.value = true
        }else{
            try {
                when {
                    purpose != ApiConstant.API_SNC_CONTACTS || purpose != ApiConstant.API_FETCH_ALL_FEEDS || purpose != ApiConstant.API_GET_CUSTOMER_INFO || purpose != ApiConstant.API_ADD_FAMILY_MEMBER || purpose != ApiConstant.API_GET_VIRTUAL_CARD_REQUEST || purpose != ApiConstant.API_ADD_MONEY_STEP2 || purpose != ApiConstant.API_LOGOUT -> {
                        Utility.showToast(errorResponseInfo.msg)

                    }
                }
            } catch (e: Exception) {
                when {
                    purpose != ApiConstant.API_SNC_CONTACTS || purpose != ApiConstant.API_ADD_FAMILY_MEMBER || purpose != ApiConstant.API_GET_VIRTUAL_CARD_REQUEST || purpose != ApiConstant.API_ADD_MONEY_STEP2 ->
                        Utility.showToast(PockketApplication.instance.getString(R.string.something_went_wrong_error1))
                }
            }
        }

    }


    override fun offLine() {
        internetError.value = true
    }

    override fun progress(isStart: Boolean, message: String) {
        progressDialog.postValue(isStart)
    }

    fun sendCrashData(){
        val customKeysAndValues = CustomKeysAndValues.Builder()
        /*FirebaseCrashlytics.getInstance().setCustomKeys{
            key()
        }*/
    }
}