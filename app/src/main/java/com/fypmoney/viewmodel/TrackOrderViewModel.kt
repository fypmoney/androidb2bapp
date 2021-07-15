package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility

/*
* This is used for card tracking
* */
class TrackOrderViewModel(application: Application) : BaseViewModel(application) {
    private var kitNumber = ObservableField<String>()
    var orderStatus = ObservableField<String>()
    var isOrderPlaced = ObservableField<Boolean>()
    var isOrderShipped = ObservableField<Boolean>()
    var isOrderDelivered = ObservableField<Boolean>()
    var isOrderOutForDelivery = ObservableField<Boolean>()
    var productResponse = MutableLiveData<GetAllProductsResponseDetails>()

    init {
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
                ApiUrl.POST,
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
                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}