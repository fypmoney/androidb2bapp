package com.fypmoney.viewmodel

import android.app.Application
import android.widget.CompoundButton
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.MemberRepository
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.model.GetMemberResponse
import com.fypmoney.model.UpdateFamilyNameResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.MemberAdapter
import com.fypmoney.view.adapter.MemberAdapterViewAll

/*
* This is used as a family settings
* */
class FamilySettingsViewModel(application: Application) : BaseViewModel(application),
    MemberAdapter.OnMemberItemClickListener {
    var onViewAllClicked = MutableLiveData<Boolean>()
    var onAddMemberClicked = MutableLiveData<Boolean>()
    var onEditFamilyNameClicked = MutableLiveData<Boolean>()
    var username = ObservableField<String>()
    var changedUserName = ObservableField<String>()
    var isNoDataFoundVisible = ObservableField(false)
    var isFamilyFiperVisible = ObservableField(false)
    val isSwitchChecked: MutableLiveData<Boolean> = MutableLiveData()
    var memberAdapter = MemberAdapter(this)
    var pendingAdapter = MemberAdapterViewAll()
    var memberRepository = MemberRepository(mDB = appDatabase)


    init {
        if (SharedPrefUtils.getString(
                getApplication(),
                SharedPrefUtils.SF_KEY_USER_FAMILY_NAME
            ) == null
        ) {
            username.set(
                Utility.getCustomerDataFromPreference()?.firstName + application.resources.getString(
                    R.string.family_settings_family_fypers
                )
            )
        } else {
            username.set(
                SharedPrefUtils.getString(
                    getApplication(),
                    SharedPrefUtils.SF_KEY_USER_FAMILY_NAME
                ) + PockketApplication.instance.resources.getString(
                    R.string.family_settings_family_fypers1
                )
            )
        }
    }

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
    /*
       * This method is used to call add member API
       * */

    fun callUpdateFamilyName() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_UPDATE_FAMILY_NAME,
                endpoint = NetworkUtil.endURL(ApiConstant.API_UPDATE_FAMILY_NAME) + changedUserName.get(),
                request_type = ApiUrl.PUT,
                param = "", onResponse = this,
                isProgressBar = true
            )
        )


    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_ADD_FAMILY_MEMBER -> {
                if (responseData is GetMemberResponse) {
                    val approveList = mutableListOf<MemberEntity>()
                    val inviteList = mutableListOf<MemberEntity>()
                    memberRepository.deleteAllMembers()
                    if (!responseData.GetMemberResponseDetails.isNullOrEmpty()) {
                        isFamilyFiperVisible.set(true)
                        memberRepository.insertAllMembers(responseData.GetMemberResponseDetails)
                        memberRepository.getAllMembersFromDatabase()?.forEach {
                            when (it.status) {
                                AppConstants.ADD_MEMBER_STATUS_APPROVED -> {
                                    approveList.add(it)
                                }
                                AppConstants.ADD_MEMBER_STATUS_INVITED -> {
                                    inviteList.add(it)
                                }
                            }

                        }

                        memberAdapter.setList(approveList)
                        pendingAdapter.setList(inviteList)

                } else {
                    isNoDataFoundVisible.set(true)
                }
                }
            }
            ApiConstant.API_UPDATE_FAMILY_NAME -> {

                if (responseData is UpdateFamilyNameResponse) {
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_FAMILY_NAME,
                        responseData.updateFamilyNameDetails.name
                    )
                    username.set(
                        responseData.updateFamilyNameDetails.name + PockketApplication.instance.resources.getString(
                            R.string.family_settings_family_fypers1
                        )
                    )
                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

    override fun onItemClick(position: Int) {
        if (position == 0) {
            onAddMemberClicked.value = true
        }
    }

    /*
    * This method is used to handle on edit family name
    * */
    fun onEditFamilyNameClicked() {
        onEditFamilyNameClicked.value = true

    }


}