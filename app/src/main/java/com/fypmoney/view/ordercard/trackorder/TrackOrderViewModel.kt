package com.fypmoney.view.ordercard.trackorder

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.adapter.OrderStatusAdapter

/*
* This is used for card tracking
* */
class TrackOrderViewModel(application: Application) : BaseViewModel(application) {
    private var kitNumber = ObservableField<String>()
    var amount = ObservableField<String>()
    var orderStatusAdapter = OrderStatusAdapter(this)
    var productResponse = MutableLiveData<GetOrderCardStatusResponseDetails>()

    val event:LiveData<TrackOrderEvent>
        get()= _event
    private val _event = LiveEvent<TrackOrderEvent>()
    init {
        kitNumber.set(SharedPrefUtils.getString(application, SharedPrefUtils.SF_KEY_KIT_NUMBER))
        callGetCardStatusApi()
    }

    fun onShippingDetailsClick(packageStatusList:PackageStatusList?){
        _event.value = TrackOrderEvent.ShippingDetailsEvent(packageStatusList)
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

    sealed class TrackOrderEvent{
         data class ShippingDetailsEvent(var packageStatusList:PackageStatusList?): TrackOrderEvent()
    }

}