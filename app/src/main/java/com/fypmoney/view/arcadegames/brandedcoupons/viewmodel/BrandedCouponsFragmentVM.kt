package com.fypmoney.view.arcadegames.brandedcoupons.viewmodel

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
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
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponResponse
import com.fypmoney.view.arcadegames.brandedcoupons.model.COUPONItem
import com.fypmoney.view.arcadegames.model.MultipleJackpotNetworkResponse
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.dialog_no_coupon.*
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.clicked

class BrandedCouponsFragmentVM(application: Application) : BaseViewModel(application) {

    //To store explore redirection code
    lateinit var productCode: String

    //To store productId on redirection
    lateinit var productId: String

    //Check user has play the arcade
    var isArcadeIsPlayed = false

    //Check user has play the arcade
    var isBrandedCouponStarted = false

    //To store mynts that will be required to play games
    var myntsDisplay: Int? = null

    //live data variable to store frequency played count
    var remainFrequency: MutableLiveData<Int> = MutableLiveData()

    var brandLogo: String? = null

    //To store total frequency data
    var frequency: Int? = 0

    var mp: MediaPlayer? = null

    //Observe spin wheel data using sealed class reward-product(SPIN_WHEEL_1000)
    val stateMynts: LiveData<MyntsState>
        get() = _stateMynts
    private val _stateMynts = MutableLiveData<MyntsState>()

    //Observe spin wheel data using sealed class reward-product(SPIN_WHEEL_1000)
    val stateTickets: LiveData<TicketState>
        get() = _stateTickets
    private val _stateTickets = MutableLiveData<TicketState>()

    //Observe spin wheel data using sealed class reward-product(SPIN_WHEEL_1000)
    val stateCash: LiveData<CashState>
        get() = _stateCash
    private val _stateCash = MutableLiveData<CashState>()

    val stateBrandedProduct: LiveData<BrandedCouponDataState>
        get() = _stateBrandedProduct
    private val _stateBrandedProduct = MutableLiveData<BrandedCouponDataState>()

    val stateMyntsBurn: LiveData<MyntsBurnState>
        get() = _stateMyntsBurn
    private val _stateMyntsBurn = LiveEvent<MyntsBurnState>()

    val stateProductDetails: LiveData<BrandedProductResponseState>
        get() = _stateProductDetails
    private val _stateProductDetails = MutableLiveData<BrandedProductResponseState>()

    val statePlayOrder: LiveData<PlayOrderState>
        get() = _statePlayOrder
    private val _statePlayOrder = LiveEvent<PlayOrderState>()

    init {
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

    fun callGetBrandedDataApi(code: String?) {
        _stateBrandedProduct.postValue(BrandedCouponDataState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_BRANDED_COUPONS_PURPOSE,
                NetworkUtil.endURL(ApiConstant.API_GET_BRANDED_COUPONS + "/${code}"),
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

            ApiConstant.API_GET_BRANDED_COUPONS_PURPOSE -> {
                if (responseData is BrandedCouponResponse) {
                    _stateBrandedProduct.value = responseData.data?.cOUPON?.get(0)
                        ?.let {
                            BrandedCouponDataState.BrandedCouponSuccess(it)
                        }

                    brandLogo = responseData.data?.cOUPON?.get(0)?.detailResource
                }
            }

            ApiConstant.API_REDEEM_REWARD -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    CoinsBurnedResponse::class.java
                )
                isArcadeIsPlayed = true

                _stateMyntsBurn.value = MyntsBurnState.MyntsBurnSuccess(array)

            }

            ApiConstant.PLAY_ORDER_API -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    SpinWheelRotateResponseDetails::class.java
                )

                _statePlayOrder.value = PlayOrderState.PlayOrderSuccess(spinDetails)

            }

            ApiConstant.REWARD_PRODUCT_DETAILS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    aRewardProductResponse::class.java
                )

                _stateProductDetails.value = BrandedProductResponseState.Success(spinDetails)
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
            ApiConstant.API_GET_BRANDED_COUPONS_PURPOSE -> {
                _stateBrandedProduct.value = BrandedCouponDataState.Error(errorResponseInfo)
            }
            ApiConstant.API_REDEEM_REWARD -> {
                _stateMyntsBurn.value = MyntsBurnState.Error(errorResponseInfo)
            }
            ApiConstant.REWARD_PRODUCT_DETAILS -> {
                _stateProductDetails.value = BrandedProductResponseState.Error(errorResponseInfo)
            }
            ApiConstant.PLAY_ORDER_API -> {
                _statePlayOrder.value = PlayOrderState.Error(errorResponseInfo)
            }
        }

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

    fun callNoCouponsDialog(context: Context) {

        val dialogNoCoupons = Dialog(context)

        dialogNoCoupons.setCancelable(false)
        dialogNoCoupons.setCanceledOnTouchOutside(false)
        dialogNoCoupons.setContentView(R.layout.dialog_no_coupon)

        val wlp = dialogNoCoupons.window?.attributes
        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialogNoCoupons.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogNoCoupons.window?.attributes = wlp

        if (brandLogo != null) {
            Utility.setImageUsingGlideWithShimmerPlaceholderWithoutNull(
                context, brandLogo,
                dialogNoCoupons.ivNoCouponBrandLogo
            )
        }
        dialogNoCoupons.btnContinue?.setOnClickListener {
            trackr {
                it.name = TrackrEvent.no_more_coupon
            }
            dialogNoCoupons.dismiss()
        }

        dialogNoCoupons.show()
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

    sealed class BrandedCouponDataState {
        object Loading : BrandedCouponDataState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : BrandedCouponDataState()
        data class BrandedCouponSuccess(var couponItem: COUPONItem) : BrandedCouponDataState()
    }

    sealed class MyntsBurnState {
        object Loading : MyntsBurnState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : MyntsBurnState()
        data class MyntsBurnSuccess(var coinsBurnedResponse: CoinsBurnedResponse) : MyntsBurnState()
    }

    sealed class BrandedProductResponseState {
        object Loading : BrandedProductResponseState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : BrandedProductResponseState()
        data class Success(var aRewardProductResponse: aRewardProductResponse) :
            BrandedProductResponseState()
    }

    sealed class PlayOrderState {
        object Loading : PlayOrderState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : PlayOrderState()
        data class PlayOrderSuccess(var spinWheelResponseDetails: SpinWheelRotateResponseDetails) :
            PlayOrderState()
    }

}