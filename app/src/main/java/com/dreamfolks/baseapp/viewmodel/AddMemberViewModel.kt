package com.dreamfolks.baseapp.viewmodel

import android.app.Application
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.application.PockketApplication
import com.dreamfolks.baseapp.base.BaseViewModel
import com.dreamfolks.baseapp.connectivity.ApiConstant
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_ADD_FAMILY_MEMBER
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_CHECK_IS_APP_USER
import com.dreamfolks.baseapp.connectivity.ApiUrl
import com.dreamfolks.baseapp.connectivity.ErrorResponseInfo
import com.dreamfolks.baseapp.connectivity.network.NetworkUtil
import com.dreamfolks.baseapp.connectivity.retrofit.ApiRequest
import com.dreamfolks.baseapp.connectivity.retrofit.WebApiCaller
import com.dreamfolks.baseapp.database.entity.ContactEntity
import com.dreamfolks.baseapp.model.AddFamilyMemberRequest
import com.dreamfolks.baseapp.model.AddFamilyMemberResponse
import com.dreamfolks.baseapp.model.BaseRequest
import com.dreamfolks.baseapp.model.IsAppUserResponse
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.util.Utility

/*
* This class is used to handle add member functionality
* */
class AddMemberViewModel(application: Application) : BaseViewModel(application) {
    var selectedCountryCodePosition = ObservableField(0)
    var minTextLength = 4
    var maxTextLength = 15
    var onFromContactClicked = MutableLiveData<Boolean>()
    var onAddMember = MutableLiveData<String>()
    var onAddMemberClicked = MutableLiveData<Boolean>()
    var onIsAppUser= MutableLiveData<String>()
    var addMemberError = MutableLiveData<Boolean>()
    var mobile = MutableLiveData<String>()
    var relationList = mutableListOf<String>()
    var selectedCountryCode = ObservableField<String>()
    var selectedRelationPosition = ObservableField(0)
    var selectedRelation = ObservableField<String>()
    var name = ObservableField<String>()
    var contactResult = ObservableField(ContactEntity())

    val clicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedRelation.set(parent?.getItemAtPosition(position) as String)
        }
    }

    init {
        relationList.add("PARENT")
        relationList.add("CHILD")
        relationList.add("SIBLING")
        relationList.add("SPOUSE")
        relationList.add("GRANDPARENT")
        relationList.add("GRANDCHILD")
        relationList.add("UNKNOWN")
        relationList.add("OTHER")
        selectedRelation.set(relationList[0])
    }

    fun onFromContactClicked() {
        onFromContactClicked.value = true
    }

    /*
    * This method is used to add member
    * */
    fun onAddMemberClicked() {
        when {
            TextUtils.isEmpty(mobile.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.phone_email_empty_error))
            }
            else -> {
                when (mobile.value) {
                    contactResult.get()!!.contactNumber -> {
                        when (contactResult.get()!!.isAppUser!!) {
                            true -> {
                                onIsAppUser.value=AppConstants.API_SUCCESS
                            }
                            else -> {
                                callIsAppUserApi()
                            }
                        }
                    }
                    else -> {
                        callIsAppUserApi()
                    }
                }
            }


        }
    }

/*
* This method is used to call add member API
* */

     fun callAddMemberApi() {
       progressDialog.value = true
        WebApiCaller.getInstance().request(
            ApiRequest(
                API_ADD_FAMILY_MEMBER,
                NetworkUtil.endURL(API_ADD_FAMILY_MEMBER),
                ApiUrl.POST,
                AddFamilyMemberRequest(
                    mobileNo = mobile.value!!.trim(),
                    name = name.get(),
                    relation = selectedRelation.get()!!
                ),
                this,
                isProgressBar = false
            )
        )


    }

/*
* This method is used to check if the user is a app user or not
* */

    private fun callIsAppUserApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = API_CHECK_IS_APP_USER,
                endpoint = NetworkUtil.endURL(API_CHECK_IS_APP_USER + mobile.value!!.trim()),
                request_type = ApiUrl.GET,
                onResponse = this,
                isProgressBar = true,
                param = BaseRequest()
            )
        )


    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        progressDialog.value = false
        when (purpose) {
            API_CHECK_IS_APP_USER -> {
                if (responseData is IsAppUserResponse) {
                    if (responseData.isAppUserResponseDetails.isAppUser!!) {
                        if (!responseData.isAppUserResponseDetails.name.isNullOrEmpty() && responseData.isAppUserResponseDetails.name != "null null")
                            name.set(responseData.isAppUserResponseDetails.name!!)
                        onIsAppUser.value = AppConstants.API_SUCCESS

                    }
                }
            }
            API_ADD_FAMILY_MEMBER -> {
                progressDialog.value = false
                if (responseData is AddFamilyMemberResponse) {
                    onAddMember.value = AppConstants.API_SUCCESS

                }
            }

        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            API_CHECK_IS_APP_USER -> {
                if (errorResponseInfo.errorCode == ApiConstant.API_CHECK_USER_ERROR_CODE)
                    name.set("")
                onIsAppUser.value = AppConstants.API_FAIL
                mobile.value = ""
            }
            API_ADD_FAMILY_MEMBER -> {
                progressDialog.value = false
                name.set("")
                mobile.value = ""
                onAddMember.value = AppConstants.API_FAIL
            }
        }

    }

}


