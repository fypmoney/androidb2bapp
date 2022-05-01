package com.fypmoney.view.recharge.viewmodel

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
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants.POSTPAID
import com.fypmoney.util.AppConstants.PREPAID
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.recharge.model.MobileNumberInfoUiModel
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.model.RechargeTypeModel
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This is used to handle user profile
* */
class MobileNumberInfoRechargeFragmentVM(application: Application) : BaseViewModel(application) {


    var operatorResponse: OperatorResponse? = null


    var mobileNumberInfoModel: MobileNumberInfoUiModel? = null

    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()
    var feedDetail: MutableLiveData<FeedDetails> = LiveEvent()

    val state: LiveData<EnterMobileNumberInfoRechargeState>
        get() = _state
    private val _state = MutableLiveData<EnterMobileNumberInfoRechargeState>()


    val event: LiveData<EnterMobileNumberInfoRechargeEvent>
        get() = _event
    private val _event = LiveEvent<EnterMobileNumberInfoRechargeEvent>()


    fun showOperatorListClicked() {
        _event.value = EnterMobileNumberInfoRechargeEvent.ShowOperatorListScreen(
            mobileNumberInfoModel?.rechargeType,
            mobileNumberInfoModel?.mobile
        )
    }

    fun onContinueClick() {
        if (!mobileNumberInfoModel?.circle.isNullOrEmpty() && operatorResponse != null) {
            if (mobileNumberInfoModel?.rechargeType == PREPAID) {
                _event.value = EnterMobileNumberInfoRechargeEvent.ShowPlanScreen(
                    operatorResponse, mobileNumberInfoModel?.mobile, mobileNumberInfoModel?.circle
                )
            } else if (mobileNumberInfoModel?.rechargeType == POSTPAID) {
                _event.value = EnterMobileNumberInfoRechargeEvent.ShowPostpaidBilScreen(
                    operatorResponse,
                    mobileNumberInfoModel?.mobile,
                    mobileNumberInfoModel?.operator,
                    mobileNumberInfoModel?.circle
                )
            }
        } else {
            _event.value = EnterMobileNumberInfoRechargeEvent.ShowOperatorListScreen(
                mobileNumberInfoModel?.rechargeType,
                mobileNumberInfoModel?.mobile
            )
        }
    }

    fun callExplporeContent(s: String?) {
        _state.value = EnterMobileNumberInfoRechargeState.Loading
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + s,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    fun callFetchFeedsApi(id: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_FEED_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_FEED_DETAILS) + id,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )

    }

    fun callFetchOfferApi(id: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_OFFER_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_OFFER_DETAILS) + id,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )

    }

    fun callGetOperatorList(postpaid: String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_OPERATOR_LIST_MOBILE,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_OPERATOR_LIST_MOBILE),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = false,
                param = RechargeTypeModel(type = postpaid)
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_Explore -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<ExploreContentResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                _state.value = EnterMobileNumberInfoRechargeState.ExploreSuccess(arrayList)

            }
            ApiConstant.API_FETCH_FEED_DETAILS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson<FeedDetails>(
                    json.get("data").toString(),
                    FeedDetails::class.java
                )
                feedDetail.postValue(array)
            }
            ApiConstant.API_FETCH_OFFER_DETAILS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<offerDetailResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                openBottomSheet.postValue(arrayList)

            }
            ApiConstant.API_GET_OPERATOR_LIST_MOBILE -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<OperatorResponse>::class.java
                )
                operatorResponse = getSelectedOpeartorId(array)
            }
        }

    }

    private fun getSelectedOpeartorId(operatorResponseList: Array<OperatorResponse>?):OperatorResponse? {
        return operatorResponseList?.find { it.name == mobileNumberInfoModel?.operator }
    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_Explore -> {
                _state.value = EnterMobileNumberInfoRechargeState.Error(
                    errorResponseInfo,
                    ApiConstant.API_Explore
                )
            }

        }

    }

    sealed class EnterMobileNumberInfoRechargeState {
        object Loading : EnterMobileNumberInfoRechargeState()
        data class Error(val errorResponseInfo: ErrorResponseInfo, val errorFromApi: String) :
            EnterMobileNumberInfoRechargeState()

        data class ExploreSuccess(val explore: ArrayList<ExploreContentResponse>) :
            EnterMobileNumberInfoRechargeState()
    }

    sealed class EnterMobileNumberInfoRechargeEvent {
        data class ShowOperatorListScreen(val rechargeType: String?, val mobileNo: String?) :
            EnterMobileNumberInfoRechargeEvent()

        data class ShowPlanScreen(
            val operator: OperatorResponse?,
            val mobile: String?,
            val circle: String?
        ) : EnterMobileNumberInfoRechargeEvent()

        data class ShowPostpaidBilScreen(
            val operatorResponse: OperatorResponse?,
            val mobile: String?,
            val operator: String?,
            val circle: String?
        ) : EnterMobileNumberInfoRechargeEvent()
    }

}