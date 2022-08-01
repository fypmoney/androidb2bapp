package com.fypmoney.view.arcadegames.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
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
import com.fypmoney.model.CoinsBurnedResponse
import com.fypmoney.model.RewardPointsSummaryResponse
import com.fypmoney.model.SpinWheelRotateResponseDetails
import com.fypmoney.view.arcadegames.model.MultipleJackpotNetworkResponse
import com.fypmoney.view.arcadegames.model.SLOTItem
import com.fypmoney.view.arcadegames.model.SlotMachineResponse
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class SlotMachineFragmentVM(application: Application) : BaseViewModel(application) {

    //To store explore redirection code
    lateinit var productCode: String

    //To store productId on redirection
    lateinit var productId: String

    var mp: MediaPlayer? = null

    //live event data to store purchase reward-product response
//    var coinsBurned: LiveEvent<CoinsBurnedResponse> = LiveEvent()

    //Check user has play the arcade
    var isArcadeIsPlayed = false

    //live data variable to store frequency played count
    var remainFrequency: MutableLiveData<Int> = MutableLiveData()

    //To store total frequency data
    var frequency: Int? = 0

    //To store total mynts value
    var myntsCount: Float? = 0f

    //To store mynts that will be required to play games
    var myntsDisplay: Int? = null

    //count to check how many events occurs
    var countDown = 0

    var spinWheelRotateResponseDetails: SpinWheelRotateResponseDetails? = null

    val state: LiveData<SlotMachineState>
        get() = _state

    private val _state = MutableLiveData<SlotMachineState>()

    init {
        remainFrequency.value = 0
        myntsCount = 0f

        callMyntsSummaryApi()
        callTotalCashRewardsEarnings()
        callTotalJackpotCards()
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

    fun callGetProductDataApi(code: String?) {
        _state.postValue(SlotMachineState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_REWARD_SLOT_MACHINE_PURPOSE,
                NetworkUtil.endURL(ApiConstant.API_GET_SLOT_MACHINE_DATA + "/${code}"),
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

    fun callPlayOrderApi(orderId: String?) {
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


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

            ApiConstant.API_GET_REWARD_EARNINGS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    totalRewardsResponse::class.java
                )
                _state.value = SlotMachineState.CashSuccess(array.amount)
            }

            ApiConstant.API_REWARD_SUMMARY -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    RewardPointsSummaryResponse::class.java
                )
                _state.value = SlotMachineState.MyntsSuccess(array.remainingPoints)
            }

            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {
                if (responseData is MultipleJackpotNetworkResponse) {
                    _state.value = SlotMachineState.TicketSuccess(responseData.data?.totalTickets)
                }
            }

            ApiConstant.API_GET_REWARD_SLOT_MACHINE_PURPOSE -> {
                if (responseData is SlotMachineResponse) {
                    _state.value = responseData.data?.sLOT?.get(0)
                        ?.let { SlotMachineState.Success(it) }
                }
            }

            ApiConstant.API_REDEEM_REWARD -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    CoinsBurnedResponse::class.java
                )
                isArcadeIsPlayed = true

                _state.value = SlotMachineState.MyntsBurnSuccess(array)

            }

            ApiConstant.PLAY_ORDER_API -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    SpinWheelRotateResponseDetails::class.java
                )

                _state.value = SlotMachineState.PlayOrderSuccess(spinDetails)

            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

    sealed class SlotMachineState {
        object Loading : SlotMachineState()

        //        data class Error(var apiName:String): SlotMachineState()
        object Error : SlotMachineState()

        data class Success(var slotItem: SLOTItem) : SlotMachineState()
        data class TicketSuccess(val totalTickets: Int?) : SlotMachineState()
        data class MyntsSuccess(val remainingMynts: Float?) : SlotMachineState()
        data class MyntsBurnSuccess(var coinsBurnedResponse: CoinsBurnedResponse) :
            SlotMachineState()

        data class CashSuccess(val totalCash: Int?) : SlotMachineState()
        data class PlayOrderSuccess(var spinWheelResponseDetails: SpinWheelRotateResponseDetails) :
            SlotMachineState()
    }

}