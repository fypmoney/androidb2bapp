package com.fypmoney.view.arcadegames.viewmodel

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.R
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponCountResponse
import com.fypmoney.view.arcadegames.model.MultipleJackpotNetworkResponse
import com.fypmoney.view.arcadegames.model.SLOTItem
import com.fypmoney.view.arcadegames.model.SlotMachineResponse
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*

class SlotMachineFragmentVM(application: Application) : BaseViewModel(application) {

    //To store explore redirection code
    lateinit var productCode: String

    //To store productId on redirection
    lateinit var productId: String

    var mp: MediaPlayer? = null

    //Check user has play the arcade
    var isArcadeIsPlayed = false

    //Check user has play the arcade
    var isSlotMachineStarted = false

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

    val stateProductDetails: LiveData<SlotMachineProductResponseState>
        get() = _stateProductDetails

    private val _stateProductDetails = MutableLiveData<SlotMachineProductResponseState>()

    //Observe spin wheel data using sealed class reward-product(SPIN_WHEEL_1000)
    val stateMynts: LiveData<MyntsSuccessState>
        get() = _stateMynts
    private val _stateMynts = MutableLiveData<MyntsSuccessState>()

    //Observe spin wheel data using sealed class reward-product(SPIN_WHEEL_1000)
    val stateTickets: LiveData<TicketSuccessState>
        get() = _stateTickets
    private val _stateTickets = MutableLiveData<TicketSuccessState>()

    //Observe spin wheel data using sealed class reward-product(SPIN_WHEEL_1000)
    val stateCash: LiveData<CashSuccessState>
        get() = _stateCash
    private val _stateCash = MutableLiveData<CashSuccessState>()

    val stateMyntsBurn: LiveData<MyntsBurnSuccessState>
        get() = _stateMyntsBurn

    private val _stateMyntsBurn = MutableLiveData<MyntsBurnSuccessState>()

    val statePlayOrder: LiveData<PlayOrderSuccessState>
        get() = _statePlayOrder
    private val _statePlayOrder = MutableLiveData<PlayOrderSuccessState>()

    val stateCouponCount: LiveData<CouponCountState>
        get() = _stateCouponCount
    private val _stateCouponCount = MutableLiveData<CouponCountState>()


    init {
        remainFrequency.value = 0
        myntsCount = 0f

        callMyntsSummaryApi()
        callTotalCashRewardsEarnings()
        callTotalJackpotCards()
        callBrandedActiveCount()

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

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

            ApiConstant.API_GET_REWARD_EARNINGS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    totalRewardsResponse::class.java
                )
                _stateCash.value = CashSuccessState.CashSuccess(array.amount)
            }

            ApiConstant.API_REWARD_SUMMARY -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    RewardPointsSummaryResponse::class.java
                )
                _stateMynts.value = MyntsSuccessState.MyntsSuccess(array.remainingPoints)
            }

            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {
                if (responseData is MultipleJackpotNetworkResponse) {
                    _stateTickets.value =
                        TicketSuccessState.TicketSuccess(responseData.data?.totalTickets)
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

                _stateMyntsBurn.value = MyntsBurnSuccessState.MyntsBurnSuccess(array)

            }

            ApiConstant.PLAY_ORDER_API -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    SpinWheelRotateResponseDetails::class.java
                )

                _statePlayOrder.value = PlayOrderSuccessState.PlayOrderSuccess(spinDetails)

            }

            ApiConstant.API_GET_ACTIVE_COUPON_COUNT_DATA -> {
                if (responseData is BrandedCouponCountResponse) {
                    _stateCouponCount.value = CouponCountState.CouponCountSuccess(responseData.data?.amount)
                }
            }

            ApiConstant.REWARD_PRODUCT_DETAILS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    aRewardProductResponse::class.java
                )

                _stateProductDetails.value = SlotMachineProductResponseState.Success(spinDetails)
            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when(purpose){
            ApiConstant.API_REDEEM_REWARD -> {
                _stateMyntsBurn.value = MyntsBurnSuccessState.Error(errorResponseInfo)
            }
            ApiConstant.API_REWARD_SUMMARY -> {
                _stateMynts.value = MyntsSuccessState.Error(errorResponseInfo)
            }
            ApiConstant.API_GET_REWARD_EARNINGS -> {
                _stateCash.value = CashSuccessState.Error(errorResponseInfo)
            }
            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {
                _stateTickets.value = TicketSuccessState.Error(errorResponseInfo)
            }
            ApiConstant.API_GET_REWARD_SLOT_MACHINE_PURPOSE -> {
                _state.value = SlotMachineState.Error(errorResponseInfo)
            }
            ApiConstant.PLAY_ORDER_API -> {
                _statePlayOrder.value = PlayOrderSuccessState.Error(errorResponseInfo)
            }
            ApiConstant.REWARD_PRODUCT_DETAILS -> {
                _stateProductDetails.value = SlotMachineProductResponseState.Error(errorResponseInfo)
            }
            ApiConstant.API_GET_ACTIVE_COUPON_COUNT_DATA -> {
                _stateCouponCount.value = CouponCountState.Error(errorResponseInfo)
            }
        }
    }

    sealed class SlotMachineState {
        object Loading : SlotMachineState()

        data class Error(var errorResponseInfo: ErrorResponseInfo) : SlotMachineState()

        data class Success(var slotItem: SLOTItem) : SlotMachineState()
    }

    sealed class MyntsBurnSuccessState {
        object Loading : MyntsBurnSuccessState()

        data class Error(var errorResponseInfo: ErrorResponseInfo) : MyntsBurnSuccessState()

        data class MyntsBurnSuccess(var coinsBurnedResponse: CoinsBurnedResponse) :
            MyntsBurnSuccessState()
    }

    sealed class PlayOrderSuccessState {
        object Loading : PlayOrderSuccessState()

        data class Error(var errorResponseInfo: ErrorResponseInfo) : PlayOrderSuccessState()

        data class PlayOrderSuccess(var spinWheelResponseDetails: SpinWheelRotateResponseDetails) :
            PlayOrderSuccessState()
    }

    sealed class SlotMachineProductResponseState {

        object Loading : SlotMachineProductResponseState()

        data class Error(var errorResponseInfo: ErrorResponseInfo) : SlotMachineProductResponseState()

        data class Success(var aRewardProductResponse: aRewardProductResponse) :
            SlotMachineProductResponseState()
    }

    sealed class TicketSuccessState {
        object Loading : TicketSuccessState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : TicketSuccessState()
        data class TicketSuccess(val totalTickets: Int?) : TicketSuccessState()
    }

    sealed class MyntsSuccessState {
        object Loading : MyntsSuccessState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : MyntsSuccessState()
        data class MyntsSuccess(val remainingMynts: Float?) : MyntsSuccessState()
    }

    sealed class CashSuccessState {
        object Loading : CashSuccessState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : CashSuccessState()
        data class CashSuccess(val totalCash: Int?) : CashSuccessState()
    }

    sealed class CouponCountState {
        object Loading : CouponCountState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : CouponCountState()
        data class CouponCountSuccess(var amount: Int?) : CouponCountState()
    }

    fun callInsufficientDialog(msg: String, context: Context) {

        val dialogInsufficientMynts = Dialog(context)

        dialogInsufficientMynts.setCancelable(false)
        dialogInsufficientMynts.setCanceledOnTouchOutside(false)
        dialogInsufficientMynts.setContentView(R.layout.dialog_rewards_insufficient)

        val wlp = dialogInsufficientMynts.window?.attributes

        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialogInsufficientMynts.setCanceledOnTouchOutside(false)
        dialogInsufficientMynts.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogInsufficientMynts.window?.attributes = wlp
        dialogInsufficientMynts.error_msg?.text = msg

        dialogInsufficientMynts.clicked?.setOnClickListener {
            trackr {
                it.name = TrackrEvent.insufficient_mynts
            }
            dialogInsufficientMynts.dismiss()
        }
        dialogInsufficientMynts.show()
    }

}