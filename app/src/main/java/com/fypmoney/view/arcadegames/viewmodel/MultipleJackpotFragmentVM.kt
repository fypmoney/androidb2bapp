package com.fypmoney.view.arcadegames.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.RewardPointsSummaryResponse
import com.fypmoney.view.arcadegames.model.JackpotDetailsItem
import com.fypmoney.view.arcadegames.model.MultipleJackpotNetworkResponse
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class MultipleJackpotFragmentVM(application: Application) : BaseViewModel(application) {

    val state: LiveData<MultipleJackpotsState>
        get() = _state
    private val _state = MutableLiveData<MultipleJackpotsState>()

    val stateMynts: LiveData<MyntsState>
        get() = _stateMynts
    private val _stateMynts = MutableLiveData<MyntsState>()

    val stateCash: LiveData<CashState>
        get() = _stateCash
    private val _stateCash = MutableLiveData<CashState>()

    init {
        callRewardSummary()
        callTotalRewardsEarnings()
        callMultipleJackpotsProduct()
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

    private fun callMultipleJackpotsProduct() {
        _state.postValue(MultipleJackpotsState.Loading)
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

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {
            ApiConstant.API_GET_REWARD_EARNINGS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson(
                    json.get("data").toString(),
                    totalRewardsResponse::class.java
                )

                _stateCash.value = CashState.Success(array.amount)



            }

            ApiConstant.API_REWARD_SUMMARY -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    RewardPointsSummaryResponse::class.java
                )

                _stateMynts.value = MyntsState.Success(array.remainingPoints)



            }

            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {
                if (responseData is MultipleJackpotNetworkResponse) {
                    _state.value = MultipleJackpotsState.Success(
                        responseData.data?.jackpotDetails,
                        responseData.data?.totalTickets
                    )
                }
            }
        }
    }

    sealed class MultipleJackpotsState {
        object Loading : MultipleJackpotsState()
        data class Success(
            val listOfJackpotDetailsItem: List<JackpotDetailsItem?>?,
            val totalTickets: Int?
        ) : MultipleJackpotsState()
        object Error : MultipleJackpotsState()
    }

    sealed class CashState{
        object Loading : CashState()
        object Error : CashState()
        data class Success(val totalCash: Int?) : CashState()
    }

    sealed class MyntsState{
        object Loading : MyntsState()
        object Error : MyntsState()
        data class Success(val remainingMynts: Float?) : MyntsState()
    }

}