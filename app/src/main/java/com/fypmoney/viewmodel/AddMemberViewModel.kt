package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
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
import com.fypmoney.model.AddFamilyMemberRequest
import com.fypmoney.model.AddFamilyMemberResponse
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.IsAppUserResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.google.android.gms.common.util.SharedPreferencesUtils
import java.lang.Exception

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
    var selectedCountryCode = ObservableField<String>()
    var selectedRelationPosition = ObservableField(0)
    var selectedRelation = ObservableField<String>()
    var parentName = ObservableField<String>()
    var contactResult = ObservableField(ContactEntity())
    var isGuarantor = ObservableField<String>()

    val clicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedRelation.set(parent?.getItemAtPosition(position) as String)
            SharedPrefUtils.putString(
                getApplication(),
                SharedPrefUtils.SF_KEY_SELECTED_RELATION,
                selectedRelation.get()
            )
        }
    }

    init {
        relationList.add(application.resources.getString(R.string.relation_drop_down_hint))
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
            TextUtils.isEmpty(parentName.get()) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.parent_name_empty_error))
            }
            TextUtils.isEmpty(mobile.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.phone_email_empty_error))
            }
            selectedRelation.get()
                .equals(PockketApplication.instance.resources.getString(R.string.relation_drop_down_hint)) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.relation_empty_error))

            }

            else -> {
                 when (mobile.value) {
                      contactResult.get()!!.contactNumber -> {
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
                    name = parentName.get(),
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
                onAddMember.value = AppConstants.API_FAIL
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
                mobile.value = contactResult.get()?.contactNumber

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

}


