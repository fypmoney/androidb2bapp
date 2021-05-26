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

/**
 *Base View Model class
 */

abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    WebApiCaller.OnWebApiResponse {
    var progressDialog = MutableLiveData(false)
    val internetError = MutableLiveData(false)

    var appDatabase: AppDatabase? = null

    init {
        appDatabase = AppDatabase.getInstance()
    }

    override fun onSuccess(purpose: String, responseData: Any) {
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        try {
            when {
                purpose != ApiConstant.API_SNC_CONTACTS || purpose != ApiConstant.API_FETCH_ALL_FEEDS || purpose != ApiConstant.API_GET_CUSTOMER_INFO -> {
                    Utility.showToast(errorResponseInfo.msg)

                }
            }
        } catch (e: Exception) {
            when {
                purpose != ApiConstant.API_SNC_CONTACTS ->
                    Utility.showToast(PockketApplication.instance.getString(R.string.something_went_wrong_error))
            }
        }
    }

    override fun offLine() {
        internetError.value = true
    }

    override fun progress(isStart: Boolean, message: String) {
        progressDialog.value = isStart
    }
}