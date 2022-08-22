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
import com.fypmoney.model.RewardPointsSummaryResponse
import com.fypmoney.view.arcadegames.brandedcoupons.model.ActiveCouponsListItem
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedActiveCouponResponse
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponCountResponse
import com.fypmoney.view.arcadegames.model.MultipleJackpotNetworkResponse
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class BrandedActiveCouponsFragmentVM(application: Application) : BaseViewModel(application) {

    val stateMynts: LiveData<MyntsState>
        get() = _stateMynts
    private val _stateMynts = MutableLiveData<MyntsState>()

    val stateTickets: LiveData<TicketState>
        get() = _stateTickets
    private val _stateTickets = MutableLiveData<TicketState>()

    val stateCash: LiveData<CashState>
        get() = _stateCash
    private val _stateCash = MutableLiveData<CashState>()

    val stateCouponCount: LiveData<CouponCountState>
        get() = _stateCouponCount
    private val _stateCouponCount = MutableLiveData<CouponCountState>()

    val stateBrandedActiveCoupons: LiveData<BrandedActiveCouponDataState>
        get() = _stateBrandedActiveCoupons
    private val _stateBrandedActiveCoupons = MutableLiveData<BrandedActiveCouponDataState>()

    init {
        callMyntsSummaryApi()
        callTotalCashRewardsEarnings()
        callTotalJackpotCards()
        callBrandedActiveCount()
        callBrandedActiveCouponData()
    }

    private fun callMyntsSummaryApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_REWARD_SUMMARY,
                NetworkUtil.endURL(ApiConstant.API_REWARD_SUMMARY),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    private fun callTotalCashRewardsEarnings() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_REWARD_EARNINGS,
                NetworkUtil.endURL(ApiConstant.API_GET_REWARD_EARNINGS),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    private fun callTotalJackpotCards() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE,
                NetworkUtil.endURL(ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    private fun callBrandedActiveCount() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_ACTIVE_COUPON_COUNT_DATA,
                NetworkUtil.endURL(ApiConstant.API_GET_ACTIVE_COUPON_COUNT_DATA),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    private fun callBrandedActiveCouponData() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_ACTIVE_COUPON_DATA,
                NetworkUtil.endURL(ApiConstant.API_GET_ACTIVE_COUPON_DATA),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {

            ApiConstant.API_GET_REWARD_EARNINGS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    totalRewardsResponse::class.java
                )
                _stateCash.value = CashState.CashSuccess(array.amount)
            }

            ApiConstant.API_REWARD_SUMMARY -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    RewardPointsSummaryResponse::class.java
                )
                _stateMynts.value = MyntsState.MyntsSuccess(array.remainingPoints)

            }

            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {
                if (responseData is MultipleJackpotNetworkResponse) {
                    _stateTickets.value = TicketState.TicketSuccess(responseData.data?.totalTickets)
                }
            }

            ApiConstant.API_GET_ACTIVE_COUPON_COUNT_DATA -> {
                if (responseData is BrandedCouponCountResponse) {
                    _stateCouponCount.value = CouponCountState.CouponCountSuccess(responseData.data?.amount)
                }
            }

            ApiConstant.API_GET_ACTIVE_COUPON_DATA -> {
                if (responseData is BrandedActiveCouponResponse) {
                    _stateBrandedActiveCoupons.value = BrandedActiveCouponDataState.BrandedCouponSuccess(responseData.data?.activeCouponsList)
                }
            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when (purpose) {

            ApiConstant.API_REWARD_SUMMARY -> {
                _stateMynts.value = MyntsState.Error(errorResponseInfo)
            }
            ApiConstant.API_GET_REWARD_EARNINGS -> {
                _stateCash.value = CashState.Error(errorResponseInfo)
            }
            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {
                _stateTickets.value = TicketState.Error(errorResponseInfo)
            }
            ApiConstant.API_GET_ACTIVE_COUPON_COUNT_DATA -> {
                _stateCouponCount.value = CouponCountState.Error(errorResponseInfo)
            }
            ApiConstant.API_GET_ACTIVE_COUPON_DATA -> {
                _stateBrandedActiveCoupons.value =
                    BrandedActiveCouponDataState.Error(errorResponseInfo)
            }

        }

    }

    sealed class TicketState {
        object Loading : TicketState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : TicketState()
        data class TicketSuccess(val totalTickets: Int?) : TicketState()
    }

    sealed class MyntsState {
        object Loading : MyntsState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : MyntsState()
        data class MyntsSuccess(val remainingMynts: Float?) : MyntsState()
    }

    sealed class CashState {
        object Loading : CashState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : CashState()
        data class CashSuccess(val totalCash: Int?) : CashState()
    }

    sealed class CouponCountState {
        object Loading : CouponCountState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : CouponCountState()
        data class CouponCountSuccess(var amount: Int?) : CouponCountState()
    }

    sealed class BrandedActiveCouponDataState {
        object Loading : BrandedActiveCouponDataState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : BrandedActiveCouponDataState()
        data class BrandedCouponSuccess(var activeCouponsListItem: List<ActiveCouponsListItem?>?) :
            BrandedActiveCouponDataState()
    }
}