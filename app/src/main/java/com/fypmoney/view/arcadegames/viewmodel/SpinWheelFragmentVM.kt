package com.fypmoney.view.arcadegames.viewmodel

import android.app.Application
import android.graphics.Color
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.arcadegames.model.MultipleJackpotNetworkResponse
import com.fypmoney.view.arcadegames.model.SectionListItem
import com.fypmoney.view.arcadegames.model.SingleSpinWheelProductNetworkResponse
import com.fypmoney.view.arcadegames.model.SpinWheelItem
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class SpinWheelFragmentVM(application: Application) : BaseViewModel(application) {

    //To store explore redirection code
    lateinit var productCode: String

    var mp: MediaPlayer? = null

    //To store productId on redirection
    lateinit var productId: String

    //live event data to store purchase reward-product response
    var coinsBurned: LiveEvent<CoinsBurnedResponse> = LiveEvent()

    //live data variable to store frequency played count
    var remainFrequency: MutableLiveData<Int> = MutableLiveData()

    //live data variable to store play order api response after successful order
    var spinWheelResponseList = MutableLiveData<SpinWheelRotateResponseDetails>()

    //To store total frequency data
    var frequency: Int? = 0

    //To store total mynts value
    var myntsCount: Float? = 0f

    //To store mynts that will be required to play games
    var myntsDisplay: Int? = null

    //live data to store product data on spin-wheel history view
    var redeemCallBackResponse = MutableLiveData<aRewardProductResponse>()

    //list to store data of wheel section-wise
    var luckyItemList: ArrayList<LuckyItem> = ArrayList()

    //live data to store errors of all apis
    var error: MutableLiveData<ErrorResponseInfo> = MutableLiveData()

    //Check user has play the arcade
    var isArcadeIsPlayed = false

    //Observe spin wheel data using sealed class reward-product(SPIN_WHEEL_1000)
    val state: LiveData<SpinWheelState>
        get() = _state
    private val _state = MutableLiveData<SpinWheelState>()

    init {
        remainFrequency.value = 0
        myntsCount = 0f

        callRewardSummary()
        callTotalRewardsEarnings()
        callTotalJackpotCards()
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

    fun setDataInSpinWheel(list: List<SectionListItem>, rewardTypeOnFailure: String?) {
        luckyItemList.clear()
        list.forEachIndexed { _, item ->

            if (item.sectionValue == "0") {
                if (rewardTypeOnFailure == "POINTS") {
                    val luckyItem1 = LuckyItem()
                    luckyItem1.icon = R.drawable.mynts_new
                    luckyItem1.color = Color.parseColor(item.colorCode)
                    luckyItemList.add(luckyItem1)
                } else {
                    val luckyItem1 = LuckyItem()
                    luckyItem1.icon = R.drawable.ticket_new
                    luckyItem1.color = Color.parseColor(item.colorCode)
                    luckyItemList.add(luckyItem1)
                }
            } else {
                val luckyItem2 = LuckyItem()
                luckyItem2.topText = Utility.convertToRs(item.sectionValue)
                luckyItem2.icon = R.drawable.cash
                luckyItem2.color = Color.parseColor(item.colorCode)
                luckyItemList.add(luckyItem2)
            }
        }
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

    fun callSingleProductApi(code: String?) {
        _state.postValue(SpinWheelState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_REWARD_SINGLE_PRODUCTS_PURPOSE,
                NetworkUtil.endURL(ApiConstant.API_GET_REWARD_SINGLE_PRODUCTS) + code,
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
                _state.value = SpinWheelState.CashSuccess(array.amount)

            }

            ApiConstant.API_REWARD_SUMMARY -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    RewardPointsSummaryResponse::class.java
                )
                _state.value = SpinWheelState.MyntsSuccess(array.remainingPoints)

            }

            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {

                if (responseData is MultipleJackpotNetworkResponse) {
                    _state.value = SpinWheelState.TicketSuccess(responseData.data?.totalTickets)
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

            ApiConstant.API_GET_REWARD_SINGLE_PRODUCTS_PURPOSE -> {
                if (responseData is SingleSpinWheelProductNetworkResponse) {
                    _state.value =
                        responseData.data?.spinWheel?.get(0)?.let { SpinWheelState.Success(it) }
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

            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {
                error.postValue(errorResponseInfo)
            }

            ApiConstant.API_REDEEM_REWARD -> {
                error.postValue(errorResponseInfo)
            }

            ApiConstant.API_GET_REWARD_SINGLE_PRODUCTS_PURPOSE -> {
                error.postValue(errorResponseInfo)
            }

            ApiConstant.PLAY_ORDER_API -> {
                error.postValue(errorResponseInfo)
            }
        }
    }

    sealed class SpinWheelState {
        object Loading : SpinWheelState()
        data class Success(var spinWheelData: SpinWheelItem) : SpinWheelState()
        data class TicketSuccess(val totalTickets: Int?) : SpinWheelState()
        data class MyntsSuccess(val remainingMynts: Float?) : SpinWheelState()
        data class CashSuccess(val totalCash: Int?) : SpinWheelState()
        object Error : SpinWheelState()
    }

}
