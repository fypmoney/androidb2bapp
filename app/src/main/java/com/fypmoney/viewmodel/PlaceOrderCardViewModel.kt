package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableArrayList
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
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility

/*
* This is used for card ordering
* */
class PlaceOrderCardViewModel(application: Application) : BaseViewModel(application) {
    var isPremiumCardVisible = ObservableField(true)
    var isProgressBarVisible = ObservableField(false)
    var amount = ObservableField("0")
    var name = MutableLiveData<String>()
    var quote = MutableLiveData<String>()
    var pin = MutableLiveData<String>()
    var houseNo = MutableLiveData<String>()
    var roadName = MutableLiveData<String>()
    var landMark = MutableLiveData<String>()
    var selectedStatePosition = MutableLiveData(0)
    var selectedCityPosition = MutableLiveData(0)
    var city = ObservableField<String>()
    var state = ObservableField<String>()
    var stateList = ObservableField<List<GetStatesResponseDetails>>()
    var cityList = ObservableField<List<GetCityResponseDetails>>()
    var stateCode = ObservableField<String>()
    var onPriceBreakupClicked = MutableLiveData<Boolean>()
    var onPlaceOrderClicked = MutableLiveData<Boolean>()
    var onUseLocationClicked = MutableLiveData<Boolean>()
    var onOrderCardSuccess = MutableLiveData<OrderCardResponseDetails>()
    var productResponse = MutableLiveData<GetAllProductsResponseDetails>()

    init {
        callGetAllProductsApi()
        callGetStateApi()

    }

    /*
       * This method is used to handle click of use pin
       * */
    fun onUseLocationClicked() {
        isProgressBarVisible.set(true)
        onUseLocationClicked.value = true

    }

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
                    city = city.get(), state = state.get(), stateCode = stateCode.get(),
                    amount = Utility.convertToPaise(amount.get()!!),
                    productId = "1"
                ),
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

    /*
    * This method is used to get all state
    * */
    private fun callGetStateApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_STATE,
                NetworkUtil.endURL(ApiConstant.API_GET_STATE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    /*
        * This method is used to get all city
        * */
    fun callGetAllCityApi(stateId: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_CITY,
                NetworkUtil.endURL(ApiConstant.API_GET_CITY + stateId),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_ORDER_CARD -> {
                if (responseData is OrderCardResponse) {
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_KIT_NUMBER,
                        responseData.orderCardResponseDetails?.kitNumber
                    )
                    onOrderCardSuccess.value = responseData.orderCardResponseDetails

                }
            }

            ApiConstant.API_GET_STATE -> {
                if (responseData is GetStatesResponse) {
                    stateList.set(responseData.getStatesResponseDetails)
                    stateCode.set(stateList.get()?.get(0)?.code)
                    state.set(stateList.get()?.get(0)?.name)

                }
            }
            ApiConstant.API_GET_CITY -> {
                if (responseData is GetCityResponse) {
                    cityList.set(responseData.getCityResponseDetails)
                    city.set(cityList.get()?.get(0)?.cityName)


                }
            }
            ApiConstant.API_GET_ALL_PRODUCTS -> {
                if (responseData is GetAllProductsResponse) {
                    productResponse.value = responseData.getAllProductsResponseDetails?.get(0)
                    val amountList =
                        Utility.convertToRs(responseData.getAllProductsResponseDetails?.get(0)?.amount)
                            .split(".")
                    amount.set(amountList[0])
                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}