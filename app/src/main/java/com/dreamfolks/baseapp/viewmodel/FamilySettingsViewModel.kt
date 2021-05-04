package com.dreamfolks.baseapp.viewmodel

import android.app.Application
import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import com.dreamfolks.baseapp.base.BaseViewModel
import com.dreamfolks.baseapp.connectivity.ApiConstant
import com.dreamfolks.baseapp.connectivity.ApiUrl
import com.dreamfolks.baseapp.connectivity.ErrorResponseInfo
import com.dreamfolks.baseapp.connectivity.network.NetworkUtil
import com.dreamfolks.baseapp.connectivity.retrofit.ApiRequest
import com.dreamfolks.baseapp.connectivity.retrofit.WebApiCaller
import com.dreamfolks.baseapp.database.MemberRepository
import com.dreamfolks.baseapp.model.GetMemberResponse
import com.dreamfolks.baseapp.view.adapter.MemberAdapter

/*
* This is used as a family settings
* */
class FamilySettingsViewModel(application: Application) : BaseViewModel(application) {
    var onViewAllClicked = MutableLiveData<Boolean>()
    var onAddMemberClicked = MutableLiveData<Boolean>()
    val isSwitchChecked: MutableLiveData<Boolean> = MutableLiveData()
    var memberAdapter = MemberAdapter()
    var memberRepository = MemberRepository(mDB = appDatabase)

    /*
    * This method is used to handle view all of mobile
    * */
    fun onViewAllClicked() {
        onViewAllClicked.value = true
    }

    /*
    * This method is used to handle add member
    * */
    fun onAddMemberClicked() {
        onAddMemberClicked.value = true
    }

    /*
    * This method is called whenever switch status is changed
    * */
    fun executeOnSwitchStatusChanged(switch: CompoundButton, isChecked: Boolean) {
        isSwitchChecked.value = isChecked
    }

    /*
    * This method is used to call add member API
    * */

    fun callGetMemberApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_ADD_FAMILY_MEMBER,
                endpoint = NetworkUtil.endURL(ApiConstant.API_ADD_FAMILY_MEMBER),
                request_type = ApiUrl.GET,
                param = "", onResponse = this,
                isProgressBar = false
            )
        )


    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_ADD_FAMILY_MEMBER -> {
                if (responseData is GetMemberResponse) {
                    memberRepository.deleteAllMembers()
                    memberRepository.insertAllMembers(responseData.GetMemberResponseDetails)
                    memberAdapter.setList(memberRepository.getAllMembersFromDatabase())
                }
            }


        }

    }

    override fun onError(purpose: String,errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose,errorResponseInfo)
    }


}