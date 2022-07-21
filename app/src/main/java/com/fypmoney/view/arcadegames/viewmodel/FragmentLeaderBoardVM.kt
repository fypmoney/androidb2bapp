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
import com.fypmoney.view.arcadegames.model.Data3
import com.fypmoney.view.arcadegames.model.LeaderBoardResponse

class FragmentLeaderBoardVM(application: Application) : BaseViewModel(application) {

    lateinit var productCode:String

    val state: LiveData<LeaderBoardState>
        get() = _state

    private val _state = MutableLiveData<LeaderBoardState>()


    fun callLeaderBoardApi(code: String?) {
        _state.postValue(LeaderBoardState.Loading)

        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_LEADERBOARD_DATA,
                NetworkUtil.endURL(ApiConstant.API_GET_LEADERBOARD_DATA) + code,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )

    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when(purpose){
            ApiConstant.API_GET_LEADERBOARD_DATA ->{
                if (responseData is LeaderBoardResponse) {
                    _state.value = LeaderBoardState.Success(responseData.data)
                }
            }
        }
    }

    sealed class LeaderBoardState {
        object Loading : LeaderBoardState()
        data class Success(var leaderBoardData: Data3?) : LeaderBoardState()
        object Error : LeaderBoardState()
    }

}