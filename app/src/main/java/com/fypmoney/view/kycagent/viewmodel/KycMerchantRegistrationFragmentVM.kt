package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.util.AppConstants
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.kycagent.delegates.StateDelegate
import com.fypmoney.view.kycagent.model.SaveShopDetailsRequest
import com.fypmoney.view.kycagent.model.SaveShopDetailsResponse

class KycMerchantRegistrationFragmentVM(application: Application) : BaseViewModel(application) {

    val saveShopDetailsRequest = SaveShopDetailsRequest(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        null
    )

    val mapOfAddress = HashMap<String, String>()

    val stateDelegate = StateDelegate()

    fun openPhotoUpload(){
        _event.value = KycMerchantRegistrationEvent.OpenPhotoUpload
    }

    var mcbPosterValue: String ?= null

    val state : LiveData<KycMerchantRegistration>
        get() = _state
    private val _state = MutableLiveData<KycMerchantRegistration>()

    val event : LiveData<KycMerchantRegistrationEvent>
        get() = _event
    private val _event = LiveEvent<KycMerchantRegistrationEvent>()

    fun saveShopDetails(saveShopDetailsRequest: SaveShopDetailsRequest){
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_SAVE_SHOP_DETAILS,
                endpoint = NetworkUtil.endURL(ApiConstant.API_SAVE_SHOP_DETAILS),
                request_type = ApiUrl.POST,
                onResponse = this,
                isProgressBar = true,
                param = saveShopDetailsRequest
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when(purpose){
            ApiConstant.API_SAVE_SHOP_DETAILS -> {
                if (responseData is SaveShopDetailsResponse){
                    _state.value = responseData.msg?.let { KycMerchantRegistration.Success(it) }
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when(purpose){
            ApiConstant.API_SAVE_SHOP_DETAILS -> {
                _state.value = KycMerchantRegistration.Error(errorResponseInfo)
            }
        }
    }

    sealed class KycMerchantRegistration{
        object Loading: KycMerchantRegistration()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : KycMerchantRegistration()
        data class Success(var message: String) : KycMerchantRegistration()
    }

    sealed class KycMerchantRegistrationEvent{
        object OpenPhotoUpload : KycMerchantRegistrationEvent()
    }

}