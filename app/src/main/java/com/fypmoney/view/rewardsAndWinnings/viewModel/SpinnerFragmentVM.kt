package com.fypmoney.view.rewardsAndWinnings.viewModel

import android.app.Application
import androidx.databinding.ObservableField
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
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class SpinnerFragmentVM(application: Application) : BaseViewModel(application) {

    var rewardHistoryList: MutableLiveData<ArrayList<RewardHistoryResponseNew>> = MutableLiveData()

    var latitude = ObservableField<Double>()
    var redeemproductDetails = LiveEvent<aRewardProductResponse>()
    val longitude = ObservableField<Double>()
    var onAddMoneyClicked = MutableLiveData(false)
    var loading = MutableLiveData(false)
    var selectedPosition = ObservableField(-1)
    var selectedPositionScratch = ObservableField(-1)
    val isApiLoading = ObservableField(true)
    val detailsCalling = ObservableField(false)
    var clickedType = ObservableField("")
    var totalmyntsClicked = MutableLiveData(false)
    var orderNumber = MutableLiveData("")
    var spinArrayList: MutableLiveData<ArrayList<aRewardProductResponse>> = MutableLiveData()
    val page = ObservableField(0)
    var scratchArrayList: MutableLiveData<ArrayList<aRewardProductResponse>> = MutableLiveData()

    var coinsBurned: LiveEvent<CoinsBurnedResponse> = LiveEvent()


    var error: MutableLiveData<ErrorResponseInfo> = MutableLiveData()
    var totalCount = ObservableField(0)

    var bottomSheetStatus: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()


    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }


    init {

        callRewardProductList()
    }


    fun onRefresh() {


    }

    fun callProductsDetailsApi(orderId: String?) {
        detailsCalling.set(true)
        orderNumber.value = orderId
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.REWARD_PRODUCT_DETAILS,
                NetworkUtil.endURL(ApiConstant.REWARD_PRODUCT_DETAILS + orderId),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )


    }


    fun callRewardsRedeem(code: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_REDEEM_REWARD,
                NetworkUtil.endURL(ApiConstant.API_REDEEM_REWARD) + code,
                ApiUrl.POST,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    fun callRewardProductList() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_REWARD_PRODUCTS_PURPOSE,
                NetworkUtil.endURL(ApiConstant.API_GET_REWARD_PRODUCTS),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }


    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }

    fun callRewardHistory() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.RewardsHistory,
                NetworkUtil.endURL(ApiConstant.RewardsHistory),
                ApiUrl.GET,
                param = QueryPaginationParams(

                    0,
                    3,
                    "createdDate,desc"
                ),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        loading.postValue(false)
        when (purpose) {


            ApiConstant.API_GET_REWARD_PRODUCTS_PURPOSE -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val dataObject = json.get("data")?.asJsonObject

                val array = Gson().fromJson<Array<aRewardProductResponse>>(
                    dataObject?.get("SPIN_WHEEL").toString(),
                    Array<aRewardProductResponse>::class.java
                )

                if (array != null) {
                    val arrayList = ArrayList(array.toMutableList())
                    spinArrayList.postValue(arrayList)
                }
                val spinarray = Gson().fromJson<Array<aRewardProductResponse>>(
                    dataObject?.get("SCRATCH_CARD").toString(),
                    Array<aRewardProductResponse>::class.java
                )


                if (spinarray != null) {
                    val scratchArray = ArrayList(spinarray.toMutableList())
                    scratchArrayList.postValue(scratchArray)
                }


            }
            ApiConstant.REWARD_PRODUCT_DETAILS -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    aRewardProductResponse::class.java
                )

                redeemproductDetails.value = spinDetails


            }
            ApiConstant.API_REDEEM_REWARD -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson<CoinsBurnedResponse>(
                    json.get("data").toString(),
                    com.fypmoney.model.CoinsBurnedResponse::class.java
                )

                coinsBurned.postValue(array)


            }


        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        loading.postValue(false)
        when (purpose) {
            ApiConstant.API_REDEEM_REWARD -> {
                error.postValue(errorResponseInfo)
            }
        }
    }


}