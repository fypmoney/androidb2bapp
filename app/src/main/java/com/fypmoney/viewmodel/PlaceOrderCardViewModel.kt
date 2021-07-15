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
import com.fypmoney.model.OrderCardRequest
import com.fypmoney.model.OrderCardResponse
import com.fypmoney.model.OrderCardResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility

/*
* This is used for card ordering
* */
class PlaceOrderCardViewModel(application: Application) : BaseViewModel(application) {
    var isPremiumCardVisible = ObservableField(true)
    var amount = ObservableField(AppConstants.CARD_PRICE)
    var name = MutableLiveData<String>()
    var quote = MutableLiveData<String>()
    var pin = MutableLiveData<String>()
    var houseNo = MutableLiveData<String>()
    var roadName = MutableLiveData<String>()
    var landMark = MutableLiveData<String>()
    var onPriceBreakupClicked = MutableLiveData<Boolean>()
    var onPlaceOrderClicked = MutableLiveData<Boolean>()
    var onOrderCardSuccess = MutableLiveData<OrderCardResponseDetails>()


    /*
    * This method is used to place the order
    * */
    fun onPriceBreakupClicked() {
        onPriceBreakupClicked.value = true


    }

    /*
    * This method is used to place the order
    * */
    fun onPlaceOrderClicked() {
        when {
            TextUtils.isEmpty(name.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.card_name_empty_error))
            }
            TextUtils.isEmpty(pin.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.pin_empty_error))
            }
            TextUtils.isEmpty(houseNo.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.hno_empty_error))
            }
            TextUtils.isEmpty(roadName.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.road_name_empty_error))
            }
            else -> {
                onPlaceOrderClicked.value = true
            }
        }


    }

    /*
    * This method is used to place order
    * */
    fun callPlaceOrderApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_ORDER_CARD,
                NetworkUtil.endURL(ApiConstant.API_ORDER_CARD),
                ApiUrl.POST,
                OrderCardRequest(
                    nameOnCard = name.value,
                    quote = quote.value,
                    paymentMode = AppConstants.ORDER_CARD_PAYMENT_MODE,
                    pincode = pin.value,
                    houseAddress = houseNo.value,
                    areaDetail = roadName.value,
                    landmark = landMark.value,
                    amount = Utility.convertToPaise(amount.get()!!),
                    productId = "1"
                ),
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_ORDER_CARD -> {
                if (responseData is OrderCardResponse) {
                    onOrderCardSuccess.value = responseData.orderCardResponseDetails
                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}