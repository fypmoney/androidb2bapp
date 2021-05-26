package com.fypmoney.viewmodel

import android.app.Application
import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.MemberRepository
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.GetMemberResponse
import com.fypmoney.view.adapter.MemberAdapter

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