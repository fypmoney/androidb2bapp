package com.fypmoney.view.arcadegames.viewmodel

import android.app.Application
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest

class FragmentSpinWheelVM(application: Application) : BaseViewModel(application) {


    init {
        callRewardSummary()
        callExploreContent()

        callTotalRewardsEarnings()
        callTotalJackpotCards()

    }

    private fun callExploreContent() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + "REWARDS",
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
                ApiConstant.API_GET_JACKPOT_CARDS,
                NetworkUtil.endURL(ApiConstant.API_GET_JACKPOT_CARDS),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }
}