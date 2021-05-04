package com.dreamfolks.baseapp.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.application.PockketApplication
import com.dreamfolks.baseapp.connectivity.ApiConstant
import com.dreamfolks.baseapp.connectivity.ErrorResponseInfo
import com.dreamfolks.baseapp.connectivity.retrofit.WebApiCaller
import com.dreamfolks.baseapp.database.AppDatabase
import com.dreamfolks.baseapp.util.Utility

/**
 *Base View Model class
 */

abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    WebApiCaller.OnWebApiResponse {
    var progressDialog = MutableLiveData<Boolean>(false)
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
                purpose != ApiConstant.API_SNC_CONTACTS || purpose != ApiConstant.API_FETCH_ALL_FEEDS -> {
                    Utility.showToast(errorResponseInfo.msg)

                }
            }
        } catch (e: Exception) {
            Utility.showToast(PockketApplication.instance.getString(R.string.something_went_wrong_error))
        }
    }

    override fun offLine() {
        internetError.value = true
    }

    override fun progress(isStart: Boolean, message: String) {
        progressDialog.value = isStart
    }
}