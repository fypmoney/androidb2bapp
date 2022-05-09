package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableArrayList
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
import com.fypmoney.database.ContactRepository
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.*
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.ContactAdapter

/*
* This is used to handle contacts
* */
class ContactViewModel(application: Application) : BaseViewModel(application) {
    var userId = SharedPrefUtils.getLong(
        application,
        SharedPrefUtils.SF_KEY_USER_ID
    )

    var contactAdapter = ContactAdapter(this, userId)

    var isClickable = ObservableField(false)
    var contactRepository = ContactRepository(mDB = appDatabase)
    var onItemClicked = MutableLiveData<ContactEntity>()
    var onIsAppUserClicked = MutableLiveData<Boolean>()
    var emptyContactListError = MutableLiveData<Boolean>()
    var contactNotFound = MutableLiveData<Boolean>(false)
    var selectedContactList = ObservableArrayList<ContactEntity>()
    var onSelectClicked = MutableLiveData<Boolean>()
    var searchedContact = ObservableField<String>()
    var countCheckIsAppUserApiCall: Int? = 0

    /*
* This method is used to get all the contacts
* */
    private fun getAllContacts() {
        Log.d("contactlist", "-1")
        contactRepository.getContactsFromDatabase().let {
            Log.d("contactlist", "0")
            try {
                progressDialog.value = false
                val sortedList =
                    contactRepository.getContactsFromDatabase() as MutableList<ContactEntity>
                if (!sortedList.isNullOrEmpty()) {
                    Log.d("contactlist", "1")
                    contactAdapter.setList(sortedList)
                    contactAdapter.newContactList?.addAll(sortedList)
                } else {
                    Log.d("contactlist", "2")
                    contactNotFound.value = true
                }
            } catch (e: Exception) {
                Log.d("contactlist", e.message.toString())
                e.printStackTrace()
            }


        }


    }

    /*
    * This will handle the click of select
    * */
    fun onSelectClicked() {
            when {
                selectedContactList.isNullOrEmpty() -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.contact_error))
                }
                else -> {
                    onSelectClicked.value = true
                }

        }

    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        searchedContact.set(s.toString())
        val list = contactAdapter.newContactList?.filter {
            it.firstName?.contains(s, ignoreCase = true) == true || it.contactNumber?.contains(s) == true
        }
        if (list?.size != 0) {
            contactAdapter.setList(list)
        } else {
            contactAdapter.newSearchList?.clear()
            val contactEntity = ContactEntity()
            contactEntity.contactNumber = searchedContact.get()
            contactEntity.firstName =
                PockketApplication.instance.getString(R.string.new_number_text)

            contactAdapter.newSearchList?.add(contactEntity)
            contactAdapter.setList(contactAdapter.newSearchList)
            if (searchedContact.get()?.length == 10) {
                callIsAppUserApi()
            }
        }
//        val list = contactAdapter.newContactList?.filter {
//            it.firstName!!.contains(s, ignoreCase = true) || it.contactNumber?.contains(s)!!
//        }
//        contactAdapter.setList(list!!)
    }


    /*
* This method is used to call contact sync API
* */ fun callContactSyncApi() {

        Log.d("contactlist", "1ee")
        val contactRequestDetailsList = mutableListOf<ContactRequestDetails>()
        contactRepository.getAllNotSyncedContacts().forEachIndexed { index, s ->
            if (s.contactNumber?.length!! > 9) {
                contactRequestDetailsList.add(s)
            }

        }
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_SNC_CONTACTS,
                NetworkUtil.endURL(ApiConstant.API_SNC_CONTACTS),
                ApiUrl.POST,
                ContactRequest(
                    contactRequestDetails = contactRequestDetailsList
                ),
                this,
                isProgressBar = false
            )
        )


    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_SNC_CONTACTS -> {
                Log.d("contactlist", responseData.toString())
                if (responseData is ContactResponse) {
                    contactRepository.updateIsSyncAndIsAppUserStatus(responseData.contactResponseDetails?.userPhoneContact)
                    getAllContacts()
                }
            }

            ApiConstant.API_CHECK_IS_APP_USER -> {
                if (responseData is IsAppUserResponse) {
                    if (responseData.isAppUserResponseDetails.isAppUser!!) {
                        contactAdapter.newSearchList?.clear()
                        val contactEntity = ContactEntity()
                        contactEntity.contactNumber = searchedContact.get()
                        contactEntity.firstName = responseData.isAppUserResponseDetails.name
                        contactEntity.isAppUser = true
                        contactEntity.userId = responseData.isAppUserResponseDetails.userId
                        emptyContactListError.value = false
                        contactAdapter.newSearchList?.add(contactEntity)
                        contactAdapter.setList(contactAdapter.newSearchList)


                    } else {
                        onIsAppUserClicked.value = true
                    }
                }
            }
        }

    }

    fun callIsAppUserApi() {
        countCheckIsAppUserApiCall = countCheckIsAppUserApiCall?.plus(1)
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_CHECK_IS_APP_USER,
                endpoint = NetworkUtil.endURL(
                    ApiConstant.API_CHECK_IS_APP_USER + searchedContact.get()?.trim()
                ),
                request_type = ApiUrl.GET,
                onResponse = this,
                isProgressBar = true,
                param = BaseRequest()
            )
        )


    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        progressDialog.value = false
        Log.d("contactlist", "addc")
        Log.d("contactlist", errorResponseInfo.toString())

        when (purpose) {
            ApiConstant.API_CHECK_IS_APP_USER -> {
                if (countCheckIsAppUserApiCall == 1) {
                    callIsAppUserApi()
                } else {
                    Utility.showToast("Please try again")
                }
            }
            ApiConstant.API_SNC_CONTACTS -> {
                emptyContactListError.value = true
                getAllContacts()
            }
        }
    }

}
