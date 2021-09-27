package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.OrderStatusAdapter

/*
* This is used for card tracking
* */
class TrackOrderViewModel(application: Application) : BaseViewModel(application) {
    private var kitNumber = ObservableField<String>()
    var amount = ObservableField<String>()
    var orderStatusAdapter = OrderStatusAdapter(this)
    var productResponse = MutableLiveData<GetOrderCardStatusResponseDetails>()

    init {
        kitNumber.set(SharedPrefUtils.getString(application, SharedPrefUtils.SF_KEY_KIT_NUMBER))
        callGetCardStatusApi()
    }

    /*
    * This method is used to track order
    * */
    private fun callGetCardStatusApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_ORDER_CARD_STATUS,
                NetworkUtil.endURL(ApiConstant.API_GET_ORDER_CARD_STATUS + kitNumber.get()),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_ORDER_CARD_STATUS -> {
                if (responseData is GetOrderCardStatusResponse) {
                    productResponse.value = responseData.GetOrderCardStatusResponseDetails
                    if (!responseData.GetOrderCardStatusResponseDetails.packageStatusList.isNullOrEmpty()) {
                        orderStatusAdapter.setList(
                            responseData.GetOrderCardStatusResponseDetails.packageStatusList
                        )
                    }
                } else {
                    Utility.showToast("Error in fetching order status,please try again")
                }
            }


        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}