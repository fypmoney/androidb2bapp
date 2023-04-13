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
import com.fypmoney.model.BaseRequest
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.kycagent.model.FetchShopDetailsResponse
import com.fypmoney.view.kycagent.model.SaveShopDetailsResponse
import com.fypmoney.view.kycagent.model.ShopData
import okhttp3.MultipartBody

class PhotoUploadKycFragmentVM(application: Application) : BaseViewModel(application) {

    val profileState: LiveData<PhotoUploadState>
        get() = _profileState
    private val _profileState = MutableLiveData<PhotoUploadState>()

    val profileEvent: LiveData<PhotoUploadEvent>
        get() = _profileEvent
    private val _profileEvent = LiveEvent<PhotoUploadEvent>()

    var shopPhotoData : MultipartBody.Part ?= null

    fun callProfilePicUploadApi(image: MultipartBody.Part) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_UPLOAD_SHOP_PIC,
                endpoint = NetworkUtil.endURL(ApiConstant.API_UPLOAD_SHOP_PIC),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = BaseRequest()
            ), image = image
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_UPLOAD_SHOP_PIC -> {
                if (responseData is FetchShopDetailsResponse) {
                    _profileState.value =
                        responseData.data?.let { PhotoUploadState.ProfilePicUpdate(it) }
                    }
            }
        }
    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_UPLOAD_SHOP_PIC -> {
                _profileState.value = PhotoUploadState.Error(errorResponseInfo)
            }
        }

    }

    sealed class PhotoUploadState{
        data class ProfilePicUpdate(var shopData: ShopData):PhotoUploadState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : PhotoUploadState()
    }

    sealed class PhotoUploadEvent{
        object OpenAgentAuthentication : PhotoUploadEvent()
    }

}