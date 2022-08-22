package com.fypmoney.view.arcadegames.viewmodel

import android.app.Application
import android.media.MediaPlayer
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
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponCountResponse
import com.fypmoney.view.arcadegames.model.MultipleJackpotNetworkResponse
import com.fypmoney.view.arcadegames.model.TreasureBoxItem
import com.fypmoney.view.arcadegames.model.TreasureBoxNetworkResponse
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class RotatingTreasureFragmentVM(application: Application) : BaseViewModel(application) {

    lateinit var productCode:String

    //To store productId on redirection
    lateinit var productId: String

    var isFirstTime: Boolean? = true

    var spinWheelRotateResponseDetails: SpinWheelRotateResponseDetails? = null

    var myntsDisplay: Int? = null
    var mp: MediaPlayer? = null

    var coinsBurned: LiveEvent<CoinsBurnedResponse> = LiveEvent()
    var remainFrequency: MutableLiveData<Int> = MutableLiveData()
    var spinWheelResponseList = MutableLiveData<SpinWheelRotateResponseDetails>()

    //live data to store product data on treasure history view
    var redeemCallBackResponse = MutableLiveData<aRewardProductResponse>()

    //live data to store errors of all apis
    var error: MutableLiveData<ErrorResponseInfo> = MutableLiveData()

    //Check user has play the arcade
    var isArcadeIsPlayed = false

    //Check user has start the arcade
    var isRotatingTreasureStarted = false

    var sectionCode: Int? = null
    var frequency: Int? = 0

    val state: LiveData<RotatingTreasureState>
        get() = _state
    private val _state = MutableLiveData<RotatingTreasureState>()

    val stateCouponCount: LiveData<CouponCountState>
        get() = _stateCouponCount
    private val _stateCouponCount = MutableLiveData<CouponCountState>()


    init {
        remainFrequency.value = 0

        callRewardSummary()
        callTotalRewardsEarnings()
        callTotalJackpotCards()
        callBrandedActiveCount()
    }

    private fun callRewardSummary() {
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

    private fun callTotalRewardsEarnings() {
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

    fun callSingleProductApi(code: String?) {
        _state.postValue(RotatingTreasureState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_TREASURE_DATA,
                NetworkUtil.endURL(ApiConstant.API_GET_TREASURE_DATA+ "/${code}"),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    fun callMyntsBurnApi(code: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_REDEEM_REWARD,
                NetworkUtil.endURL(ApiConstant.API_REDEEM_REWARD) + code,
                ApiUrl.POST,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    /*
  * This method is used to call spin wheel api
  * */
    fun callSpinWheelApi(orderId: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.PLAY_ORDER_API,
                NetworkUtil.endURL(ApiConstant.PLAY_ORDER_API + orderId),
                ApiUrl.POST,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    /*
    * This method is used to call get rewards api
    */

    fun callProductsDetailsApi(orderId: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.REWARD_PRODUCT_DETAILS,
                NetworkUtil.endURL(ApiConstant.REWARD_PRODUCT_DETAILS + orderId),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )

    }

    sealed class RotatingTreasureState {
        object Loading : RotatingTreasureState()
        data class Success(var treasureBoxItem: TreasureBoxItem) : RotatingTreasureState()
        data class TicketSuccess(val totalTickets: Int?) : RotatingTreasureState()
        data class MyntsSuccess(val remainingMynts: Float?) : RotatingTreasureState()
//        data class MyntsBurnSuccess(var coinsBurnedResponse: CoinsBurnedResponse) : RotatingTreasureState()
        data class CashSuccess(val totalCash: Int?) : RotatingTreasureState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : RotatingTreasureState()
    }

    sealed class CouponCountState {
        object Loading : CouponCountState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : CouponCountState()
        data class CouponCountSuccess(var amount: Int?) : CouponCountState()
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

                _state.value = RotatingTreasureState.CashSuccess(array.amount)

            }

            ApiConstant.API_REWARD_SUMMARY -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    RewardPointsSummaryResponse::class.java
                )

                _state.value = RotatingTreasureState.MyntsSuccess(array.remainingPoints)
            }

            ApiConstant.API_GET_TREASURE_DATA -> {
                if (responseData is TreasureBoxNetworkResponse) {
                    _state.value = responseData.data?.treasureBox?.get(0)
                        ?.let { RotatingTreasureState.Success(it) }
                }
            }

            ApiConstant.API_REDEEM_REWARD -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    CoinsBurnedResponse::class.java
                )
                isArcadeIsPlayed = true

                coinsBurned.postValue(array)

            }

            ApiConstant.API_GET_ACTIVE_COUPON_COUNT_DATA -> {
                if (responseData is BrandedCouponCountResponse) {
                    _stateCouponCount.value = CouponCountState.CouponCountSuccess(responseData.data?.amount)
                }
            }

            ApiConstant.PLAY_ORDER_API -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    SpinWheelRotateResponseDetails::class.java
                )

                spinWheelResponseList.value = spinDetails

            }

            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {

                if (responseData is MultipleJackpotNetworkResponse) {
                    _state.value = RotatingTreasureState.TicketSuccess(responseData.data?.totalTickets)
                }
            }

            ApiConstant.REWARD_PRODUCT_DETAILS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    aRewardProductResponse::class.java
                )

                redeemCallBackResponse.value = spinDetails
            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_GET_REWARD_EARNINGS -> {
                error.postValue(errorResponseInfo)
            }

            ApiConstant.API_REWARD_SUMMARY -> {
                error.postValue(errorResponseInfo)
            }

            ApiConstant.API_GET_TREASURE_DATA -> {
                error.postValue(errorResponseInfo)
                _state.value = RotatingTreasureState.Error(errorResponseInfo)
            }

            ApiConstant.API_REDEEM_REWARD -> {
                error.postValue(errorResponseInfo)
            }

            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {
                error.postValue(errorResponseInfo)
            }

            ApiConstant.PLAY_ORDER_API -> {
                error.postValue(errorResponseInfo)
            }

            ApiConstant.API_GET_ACTIVE_COUPON_COUNT_DATA -> {
                _stateCouponCount.value = CouponCountState.Error(errorResponseInfo)
            }
        }
    }

}