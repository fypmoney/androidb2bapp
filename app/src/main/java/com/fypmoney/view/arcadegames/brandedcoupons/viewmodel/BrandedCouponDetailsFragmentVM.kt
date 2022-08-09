package com.fypmoney.view.arcadegames.brandedcoupons.viewmodel

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
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponDetailsResponse
import com.fypmoney.view.arcadegames.brandedcoupons.model.CouponDetailsData

class BrandedCouponDetailsFragmentVM(application: Application) : BaseViewModel(application) {

    lateinit var couponCode: String

    val stateBrandedCoupon: LiveData<BrandedCouponDetailsState>
        get() = _stateBrandedCoupon

    private val _stateBrandedCoupon = MutableLiveData<BrandedCouponDetailsState>()


    fun callRewardCouponsApi(code: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_COUPON_REWARD_DATA,
                NetworkUtil.endURL(ApiConstant.API_GET_COUPON_REWARD_DATA + code),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {
            ApiConstant.API_GET_COUPON_REWARD_DATA -> {
                if (responseData is BrandedCouponDetailsResponse) {
                    _stateBrandedCoupon.value =
                        responseData.data?.let {
                            BrandedCouponDetailsState.BrandedCouponDetailsSuccess(
                                it
                            )
                        }
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when (purpose) {
            ApiConstant.API_GET_COUPON_REWARD_DATA -> {
                _stateBrandedCoupon.value = BrandedCouponDetailsState.Error(errorResponseInfo)
            }
        }

    }

    sealed class BrandedCouponDetailsState {
        object Loading : BrandedCouponDetailsState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : BrandedCouponDetailsState()
        data class BrandedCouponDetailsSuccess(var couponDetailsData: CouponDetailsData) :
            BrandedCouponDetailsState()
    }

}