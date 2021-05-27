package com.fypmoney.viewmodel

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.MemberRepository
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.model.*
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.MemberAdapterViewAll

/*
* This is used to handle members of the family
* */
class MemberViewModel(application: Application) : BaseViewModel(application){
    var memberAdapter = MemberAdapterViewAll()
    var onSendLinkClicked = MutableLiveData<Boolean>()
    var onAddMemberClicked = MutableLiveData<Boolean>()
    var onLeaveMemberSuccess = MutableLiveData<Boolean>()
    var onLeaveMemberClicked = MutableLiveData<Int>()
    var memberRepository = MemberRepository(mDB = appDatabase)
    var memberDetailsData = ObservableField<MemberEntity>()
    var isMemberCardVisible = ObservableField(true)

    init {
        val list = memberRepository.getAllMembersFromDatabase()
        if (list!!.isNotEmpty()) {
            memberAdapter.setList(memberRepository.getAllMembersFromDatabase())
        } else {
            isMemberCardVisible.set(false)
        }
    }

    /*
       * This method is used to handle add member
       * */
    fun onAddMemberClicked() {
        onAddMemberClicked.value = true
    }

    /*
    * This is used to handle send link
    * */
    fun onSendLinkClicked() {
        onSendLinkClicked.value = true
    }
    /*
     * This is used to handle leave member
     * */

    fun onLeaveMemberClicked(view: View) {
        onLeaveMemberClicked.value = view.id
    }

   /* override fun onMemberClick(id: Int, memberDetails: MemberEntity) {
        memberDetailsData.set(memberDetails)
        onLeaveMemberClicked.value = id
    }*/

    /*
     * This method is used to call leave family API
     * */
    fun callLeaveFamilyApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_LEAVE_FAMILY_MEMBER,
                NetworkUtil.endURL(ApiConstant.API_LEAVE_FAMILY_MEMBER),
                ApiUrl.PUT,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    /*
   * This method is used to call remove family member API
   * */
    fun callRemoveMemberApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_REMOVE_FAMILY_MEMBER,
                NetworkUtil.endURL(ApiConstant.API_REMOVE_FAMILY_MEMBER + memberDetailsData.get()?.userId?.toInt()),
                ApiUrl.DELETE,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_LEAVE_FAMILY_MEMBER -> {
                if (responseData is LeaveFamilyResponse) {
                    isMemberCardVisible.set(false)
                    memberRepository.deleteAllMembers()
                    onLeaveMemberSuccess.value = true
                    Utility.showToast(responseData.msg!!)
                }
            }
            ApiConstant.API_REMOVE_FAMILY_MEMBER -> {
                if (responseData is RemoveFamilyResponse) {
                    onLeaveMemberSuccess.value = true
                    callGetMemberApi()
                }
            }
            ApiConstant.API_ADD_FAMILY_MEMBER -> {
                if (responseData is GetMemberResponse) {
                    memberRepository.deleteAllMembers()
                    memberRepository.insertAllMembers(responseData.GetMemberResponseDetails)
                    memberAdapter.setList(memberRepository.getAllMembersFromDatabase())
                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }
    /*
       * This method is used to call add member API
       * */

    private fun callGetMemberApi() {
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

}