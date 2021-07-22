package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.GetAllProductsResponse
import com.fypmoney.model.GetAllProductsResponseDetails
import com.fypmoney.model.GetOrderCardStatusResponse
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.OrderStatusAdapter

/*
* This is used for card tracking
* */
class TrackOrderViewModel(application: Application) : BaseViewModel(application) {
    private var kitNumber = ObservableField<String>()
    var orderStatus = ObservableField<String>()
    var amount = ObservableField<String>()
    var itemTotal = ObservableField<String>()
    var nameOfProduct = ObservableField<String>()
    var taxAmount = ObservableField<String>()
    var isOrderPlaced = ObservableField<Boolean>()
    var isOrderShipped = ObservableField<Boolean>()
    var isOrderDelivered = ObservableField<Boolean>()
    var isOrderOutForDelivery = ObservableField<Boolean>()
    var orderStatusAdapter = OrderStatusAdapter(this)
    var productResponse = MutableLiveData<GetAllProductsResponseDetails>()

    init {
        kitNumber.set(SharedPrefUtils.getString(application, SharedPrefUtils.SF_KEY_KIT_NUMBER))
        callGetAllProductsApi()
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

    /*
    * This method is used to get all cards
    * */
    private fun callGetAllProductsApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_ALL_PRODUCTS,
                NetworkUtil.endURL(ApiConstant.API_GET_ALL_PRODUCTS),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_ORDER_CARD_STATUS -> {
                if (responseData is GetOrderCardStatusResponse) {

                    orderStatusAdapter.setList(responseData.GetOrderCardStatusResponseDetails)
                }
            }
            ApiConstant.API_GET_ALL_PRODUCTS -> {
                if (responseData is GetAllProductsResponse) {
                    orderStatus.set(responseData.getAllProductsResponseDetails?.get(0)?.status)
                    productResponse.value = responseData.getAllProductsResponseDetails?.get(0)
                    amount.set(Utility.convertToRs(responseData.getAllProductsResponseDetails?.get(0)?.amount))
                    nameOfProduct.set(
                        responseData.getAllProductsResponseDetails?.get(
                            0
                        )?.name
                    )
                    taxAmount.set(
                        Utility.convertToRs(
                            responseData.getAllProductsResponseDetails?.get(
                                0
                            )?.taxAmount
                        )
                    )
                    itemTotal.set(
                        Utility.convertToRs(
                            (responseData.getAllProductsResponseDetails?.get(0)?.amount?.toInt()!! - (responseData.getAllProductsResponseDetails[0].packagingCharge?.toInt()!! + responseData.getAllProductsResponseDetails.get(
                                0
                            ).deleivceryCharge?.toInt()!! + responseData.getAllProductsResponseDetails[0].taxAmount?.toInt()!!)).toString()
                        )
                    )

                }
            }


        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}