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
    var isDiscountVisible = ObservableField(0)
    var isCityVisible = ObservableField<Boolean>()
    var stateList = ObservableField<List<GetStatesResponseDetails>>()
    var cityList = ObservableField<List<GetCityResponseDetails>>()
    var stateCode = ObservableField<String>()
    var onPriceBreakupClicked = MutableLiveData<Boolean>()
    var onPlaceOrderClicked = MutableLiveData<Boolean>()
    var onUseLocationClicked = MutableLiveData<Boolean>()
    var onOrderCardSuccess = MutableLiveData<OrderCardResponseDetails>()
    var productResponse = MutableLiveData<GetAllProductsResponseDetails?>()
    val stateInitialList = mutableListOf<GetStatesResponseDetails>()
    val cityInitialList = mutableListOf<GetCityResponseDetails>()

    init {
        stateInitialList.add(GetStatesResponseDetails(name = application.getString(R.string.state_hint)))
        stateList.set(stateInitialList)
        cityInitialList.add(GetCityResponseDetails(cityName = application.getString(R.string.city_hint_val)))
        cityList.set(cityInitialList)
        if (Utility.getCustomerDataFromPreference()?.cardProductCode == null) {
            callGetAllProductsApi()
        } else {
            isDiscountVisible.set(1)
            callGetAllProductsByCodeApi()
        }

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
            state.get() == PockketApplication.instance.getString(R.string.state_hint) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.state_empty_error))
            }
            city.get() == PockketApplication.instance.getString(R.string.city_hint_val) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.city_empty_error))
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
                    amount = productResponse.value?.mrp,
                    productId = productResponse.value?.id,
                    productMasterCode = productResponse.value?.code,
                    taxMasterCode = productResponse.value?.taxMasterCode,
                    totalTax = productResponse.value?.totalTax,
                    discount = productResponse.value?.discount,
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
                NetworkUtil.endURL(ApiConstant.API_GET_ALL_PRODUCTS + AppConstants.ORDER_CARD_PHYSICAL_CARD_CODE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    /*
    * This method is used to get all cards
    * */
    private fun callGetAllProductsByCodeApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_ALL_PRODUCTS_BY_CODE,
                NetworkUtil.endURL(ApiConstant.API_GET_ALL_PRODUCTS_BY_CODE + Utility.getCustomerDataFromPreference()?.cardProductCode),
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
                    responseData.getStatesResponseDetails?.forEach { stateInitialList.add(it) }
                    stateList.set(stateInitialList)
                    stateCode.set(stateList.get()?.get(0)?.code)
                    state.set(stateList.get()?.get(0)?.name)

                }
            }
            ApiConstant.API_GET_CITY -> {
                if (responseData is GetCityResponse) {
                    if (!responseData.getCityResponseDetails.isNullOrEmpty()) {
                        cityInitialList.clear()
                        cityInitialList.add(
                            GetCityResponseDetails(
                                cityName = PockketApplication.instance.getString(
                                    R.string.city_hint_val
                                )
                            )
                        )
                        responseData.getCityResponseDetails.forEach { cityInitialList.add(it) }
                        cityList.set(cityInitialList)
                        cityList.notifyChange()
                        city.set(cityList.get()?.get(0)?.cityName)
                    }

                }
            }
            ApiConstant.API_GET_ALL_PRODUCTS, ApiConstant.API_GET_ALL_PRODUCTS_BY_CODE -> {
                if (responseData is GetAllProductsResponse) {
                    if (!responseData.getAllProductsResponseDetails?.isNullOrEmpty()!!) {
                        productResponse.value = responseData.getAllProductsResponseDetails[0]
                        amount.set(
                            Utility.convertToRs(productResponse.value?.basePrice)
                                ?.split(".")?.get(0)
                        )

                    }
                }

            }
        }


    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}