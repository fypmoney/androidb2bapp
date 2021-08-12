package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiConstant.API_ADD_FAMILY_MEMBER
import com.fypmoney.connectivity.ApiConstant.API_CHECK_IS_APP_USER
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.RelationAdapter
import com.google.android.gms.common.util.SharedPreferencesUtils
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

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
    var onIsAppUser = MutableLiveData<String>()
    var addMemberError = MutableLiveData<Boolean>()
    var mobile = MutableLiveData<String>()
    var relationList = mutableListOf<String>()
    var iconList = mutableListOf<Int>()
    var relationAdapter = RelationAdapter(this)
    var selectedCountryCode = ObservableField<String>()
    var selectedRelationPosition = ObservableField(0)
    var parentName = ObservableField<String>()
    var contactResult = ObservableField(ContactEntity())
    var isGuarantor = ObservableField<String>()
    var selectedRelationList = ObservableArrayList<RelationModel>()


    init {
        val list = PockketApplication.instance.resources.getStringArray(R.array.relationNameList)
        val iconList = PockketApplication.instance.resources.getIntArray(R.array.relationIconList)
        val relationModelList = ArrayList<RelationModel>()

        list.forEachIndexed { index, it ->
            val relationModel = RelationModel()
            relationModel.relationName = it
            relationModel.relationImage = iconList[index]
            relationModelList.add(relationModel)
        }

        relationAdapter.setList(relationModelList)

    }

    fun onFromContactClicked() {
        onFromContactClicked.value = true
    }

    /*
    * This method is used to add member
    * */
    fun onAddMemberClicked() {
        when {
            TextUtils.isEmpty(parentName.get()) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.member_name_empty_error))
            }
            TextUtils.isEmpty(mobile.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.phone_email_empty_error))
            }
            selectedRelationList.isNullOrEmpty() -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.relation_empty_error))

            }

            else -> {
                if (!contactResult.get()?.contactNumber.isNullOrEmpty()) {
                    when (mobile.value) {
                        Utility.removePlusOrNineOneFromNo(contactResult.get()?.contactNumber) -> {
                            when (contactResult.get()!!.isAppUser!!) {
                                true -> {
                                    onIsAppUser.value = AppConstants.API_SUCCESS
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
                } else {

                    callIsAppUserApi()
                }
            }


        }
    }

/*
* This method is used to call add member API
* */

    fun callAddMemberApi() {
        SharedPrefUtils.putString(
            getApplication(),
            SharedPrefUtils.SF_KEY_SELECTED_RELATION,
            selectedRelationList.get(0).relationName
        )
        progressDialog.value = true
        var relation = selectedRelationList.get(0).relationName?.toUpperCase(Locale.getDefault())!!
        if (relation == "KIDS") {
            relation = "CHILD"

        } else if (relation == "GRAND CHILD") {
            relation = "GRANDCHILD"

        } else if (relation == "GRAND PARENTS") {
            relation = "GRANDPARENT"

        }

        WebApiCaller.getInstance().request(
            ApiRequest(
                API_ADD_FAMILY_MEMBER,
                NetworkUtil.endURL(API_ADD_FAMILY_MEMBER),
                ApiUrl.POST,
                AddFamilyMemberRequest(
                    mobileNo = mobile.value!!.trim(),
                    name = parentName.get(),
                    relation = relation
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
                        onIsAppUser.value = AppConstants.API_SUCCESS

                    } else {
                        onIsAppUser.value = AppConstants.API_FAIL
                        mobile.value = ""
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
                if (errorResponseInfo.errorCode == ApiConstant.API_CHECK_USER_ERROR_CODE) {
                    onIsAppUser.value = AppConstants.API_FAIL
                    mobile.value = ""
                }
            }
            API_ADD_FAMILY_MEMBER -> {
                progressDialog.value = false
                mobile.value = ""
                parentName.set("")
                onAddMember.value = errorResponseInfo.msg
            }
        }

    }

    /*
    * This is used to set selected response
    * */
    fun setResponseAfterContactSelected(contactEntity: ContactEntity?) {
        try {
            if (contactEntity?.contactNumber != null) {
                contactResult.set(contactEntity)
                if (contactResult.get()!!.lastName.isNullOrEmpty()) {
                    parentName.set(contactResult.get()?.firstName)
                } else {
                    parentName.set(contactResult.get()?.firstName + " " + contactResult.get()?.lastName)
                }
                mobile.value =
                    Utility.removePlusOrNineOneFromNo(contactResult.get()?.contactNumber!!)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

}


