package com.fypmoney.view.arcadegames.viewmodel

import android.app.Application
import android.graphics.Color
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
import com.fypmoney.view.arcadegames.model.SectionListItem
import com.fypmoney.view.arcadegames.model.SingleSpinWheelProductNetworkResponse
import com.fypmoney.view.arcadegames.model.SpinWheelItem
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.rewardsAndWinnings.model.TotalJackpotResponse
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class FragmentSpinWheelVM(application: Application) : BaseViewModel(application) {

    lateinit var productCode:String
    var rewardSummaryStatus: MutableLiveData<RewardPointsSummaryResponse> = MutableLiveData()
    var totalRewardsResponse: MutableLiveData<totalRewardsResponse> = MutableLiveData()
    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()
    var totalJackpotAmount: MutableLiveData<TotalJackpotResponse> = MutableLiveData()
    var coinsBurned: LiveEvent<CoinsBurnedResponse> = LiveEvent()
    var remainFrequency: MutableLiveData<Int> = MutableLiveData()
    var spinWheelResponseList = MutableLiveData<SpinWheelRotateResponseDetails>()

    var noOfJackpotTickets: Int? = null
    var frequency: Int? = 0
    var isSectionArrayListEmpty: Boolean? = null
    var myntsCount: Int? = 0
    var cashCount: Int? = 0

    var luckyItemList: ArrayList<LuckyItem> = ArrayList()
    var error: MutableLiveData<ErrorResponseInfo> = MutableLiveData()

    val enableSpin = MutableLiveData<Boolean>()
//    val onPlayClicked = MutableLiveData<Boolean>()

    val state: LiveData<SpinWheelState>
        get() = _state
    private val _state = MutableLiveData<SpinWheelState>()

    init {
        remainFrequency.value = 0
        noOfJackpotTickets = 0
        myntsCount = 0
        cashCount = 0
        isSectionArrayListEmpty = true

        callRewardSummary()
//        callExploreContent()
        callTotalRewardsEarnings()
        callTotalJackpotCards()
    }

//    private fun callExploreContent() {
//        WebApiCaller.getInstance().request(
//            ApiRequest(
//                ApiConstant.API_Explore,
//                NetworkUtil.endURL(ApiConstant.API_Explore) + "REWARDS",
//                ApiUrl.GET,
//                BaseRequest(),
//                this, isProgressBar = false
//            )
//        )
//    }

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
                ApiConstant.API_GET_JACKPOT_CARDS,
                NetworkUtil.endURL(ApiConstant.API_GET_JACKPOT_CARDS),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    fun setDataInSpinWheel(list: List<SectionListItem>, rewardTypeOnFailure: String?) {
        luckyItemList.clear()
        list.forEachIndexed { pos, item ->

            if (item.sectionValue == "0") {
                if (rewardTypeOnFailure == "POINTS"){
                    val luckyItem1 = LuckyItem()
                    luckyItem1.icon = R.drawable.mynt
                    luckyItem1.color = Color.parseColor(item.colorCode)
                    luckyItemList.add(luckyItem1)
                }else{
                    val luckyItem1 = LuckyItem()
                    luckyItem1.icon = R.drawable.ticket
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
    * This method is used to play wheel
    * */
//    fun onPlayClicked() {
//        onPlayClicked.value = true
//    }

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
* */

//    fun callProductsDetailsApi(orderId: String?) {
//        WebApiCaller.getInstance().request(
//            ApiRequest(
//                ApiConstant.REWARD_PRODUCT_DETAILS,
//                NetworkUtil.endURL(ApiConstant.REWARD_PRODUCT_DETAILS + orderId),
//                ApiUrl.GET,
//                BaseRequest(),
//                this, isProgressBar = true
//            )
//        )
//    }

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
                ApiConstant.API_GET_REWARD_SINGLE_PRODUCTS,
                NetworkUtil.endURL(ApiConstant.API_GET_REWARD_SINGLE_PRODUCTS) + code,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
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

//            ApiConstant.API_Explore -> {
//                val json = JsonParser.parseString(responseData.toString()) as JsonObject
//
//                val array = Gson().fromJson(
//                    json.get("data").toString(),
//                    Array<ExploreContentResponse>::class.java
//                )
//                val arrayList = ArrayList(array.toMutableList())
//                rewardHistoryList.postValue(arrayList)
//            }

            ApiConstant.API_GET_JACKPOT_CARDS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                if (json.get("data").toString() != "[]") {
                    val array = Gson().fromJson(
                        json.get("data").toString(),
                        TotalJackpotResponse::class.java
                    )
                    totalJackpotAmount.postValue(array)
                }
            }

            ApiConstant.API_REDEEM_REWARD -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    CoinsBurnedResponse::class.java
                )
                coinsBurned.postValue(array)
            }

            ApiConstant.API_GET_REWARD_SINGLE_PRODUCTS -> {
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

//                onPlayClicked()
//                played.set(true)
//                fromWhich.set(AppConstants.ERROR_TYPE_AFTER_SPIN)


            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_REDEEM_REWARD -> {
                error.postValue(errorResponseInfo)
            }
        }
    }

    sealed class SpinWheelState {
        object Loading : SpinWheelState()
        data class Success(var spinWheelData: SpinWheelItem) : SpinWheelState()
        object Error : SpinWheelState()
    }
}
