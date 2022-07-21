package com.fypmoney.view.arcadegames.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.CoinsBurnedResponse
import com.fypmoney.model.RewardPointsSummaryResponse
import com.fypmoney.model.SpinWheelRotateResponseDetails
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.arcadegames.model.MultipleJackpotNetworkResponse
import com.fypmoney.view.arcadegames.model.TreasureBoxItem
import com.fypmoney.view.arcadegames.model.TreasureBoxNetworkResponse
import com.fypmoney.view.rewardsAndWinnings.model.TotalJackpotResponse
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentRotatingTreasureVM(application: Application) : BaseViewModel(application) {

    lateinit var productCode:String
    var rewardSummaryStatus: MutableLiveData<RewardPointsSummaryResponse> = MutableLiveData()
    var totalRewardsResponse: MutableLiveData<totalRewardsResponse> = MutableLiveData()
    var totalJackpotAmount: MutableLiveData<TotalJackpotResponse> = MutableLiveData()
    var coinsBurned: LiveEvent<CoinsBurnedResponse> = LiveEvent()
    var listAddedCount: Int = 0
    var btnClickCount: Int = 0
    //    var rotatingTreasureResponseList = MutableLiveData<Rotati>
    var remainFrequency: MutableLiveData<Int> = MutableLiveData()
    var spinWheelResponseList = MutableLiveData<SpinWheelRotateResponseDetails>()
    var noOfJackpotTickets: Int? = null
    var positionSectionId: MutableLiveData<Int> = MutableLiveData()

    var sectionId: Int? = null
    //    var noOfJackpotTickets: Int? = null
    var frequency: Int? = 0

//    var currentRotateCount: Int? = 0

    val state: LiveData<RotatingTreasureState>
        get() = _state
    private val _state = MutableLiveData<RotatingTreasureState>()

    val stateRotTicket: LiveData<RotatingTicket>
        get() = _stateRotTicket
    private val _stateRotTicket = MutableLiveData<RotatingTicket>()


    init {
        remainFrequency.value = 0
        btnClickCount = 0
        listAddedCount = 0
        noOfJackpotTickets = 0
//        currentRotateCount = 0

        callRewardSummary()
        callTotalRewardsEarnings()
        callTotalJackpotCards()
        callSingleProductApi("TREASURE_BOX_101")
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

    private fun callSingleProductApi(code: String?) {
        _state.postValue(RotatingTreasureState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_TREASURE_DATA,
                NetworkUtil.endURL(ApiConstant.API_GET_TREASURE_DATA+ "/${code}"),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
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

    sealed class RotatingTreasureState {
        object Loading : RotatingTreasureState()
        data class Success(var treasureBoxItem: TreasureBoxItem) : RotatingTreasureState()
        object Error : RotatingTreasureState()
    }

    sealed class RotatingTicket {
        object Loading : RotatingTicket()
        data class Success(val totalTickets: Int?) : RotatingTicket()
        object Error : RotatingTicket()
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {

            ApiConstant.API_GET_REWARD_EARNINGS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse::class.java
                )

                totalRewardsResponse.postValue(array)
            }

            ApiConstant.API_REWARD_SUMMARY -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    RewardPointsSummaryResponse::class.java
                )

                rewardSummaryStatus.postValue(array)
            }

//            ApiConstant.API_GET_JACKPOT_CARDS -> {
//                val json = JsonParser.parseString(responseData.toString()) as JsonObject
//                if (json.get("data").toString() != "[]") {
//                    val array = Gson().fromJson(
//                        json.get("data").toString(),
//                        TotalJackpotResponse::class.java
//                    )
//                    totalJackpotAmount.postValue(array)
//                }
//            }

//            ApiConstant.API_GET_REWARD_SINGLE_PRODUCTS -> {
//                if (responseData is TreasureBoxNetworkResponse) {
//                    _state.value = responseData.data?.treasureBox?.get(0)
//                        ?.let { RotatingTreasureState.Success(it) }
//                }
//            }

            ApiConstant.API_GET_TREASURE_DATA -> {
                if (responseData is TreasureBoxNetworkResponse) {
                    _state.value = responseData.data?.treasureBox?.get(0)
                        ?.let { RotatingTreasureState.Success(it) }
                    Log.d("TreasureBoxData", responseData.data.toString())
                }
            }

            ApiConstant.API_REDEEM_REWARD -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    CoinsBurnedResponse::class.java
                )
                viewModelScope.launch {
                    delay(2000)
                    coinsBurned.postValue(array)


                }
            }

            ApiConstant.PLAY_ORDER_API -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    SpinWheelRotateResponseDetails::class.java
                )

                spinWheelResponseList.value = spinDetails
//                played.set(true)
//                fromWhich.set(AppConstants.ERROR_TYPE_AFTER_SPIN)


            }

            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {

                if (responseData is MultipleJackpotNetworkResponse) {
                    _stateRotTicket.value = RotatingTicket.Success(responseData.data?.totalTickets)
                }
            }

        }
    }

}