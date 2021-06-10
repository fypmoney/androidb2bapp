package com.fypmoney.viewmodel

import android.app.Application
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
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.ContactAdapter
import com.fypmoney.view.adapter.ContactListAdapter
import java.lang.Exception

/*
* This is used to handle contacts
* */
class ContactListViewModel(application: Application) : BaseViewModel(application) {
    var contactAdapter = ContactListAdapter(this)
    var isClickable = ObservableField(false)
    var countCheckIsAppUserApiCall: Int? = 0
    var searchedContact = ObservableField<String>()
    var searchedName = ObservableField<String>()
    var isApiError = ObservableField(false)
    var contactRepository = ContactRepository(mDB = appDatabase)
    var onItemClicked = MutableLiveData<ContactEntity>()
    var onIsAppUserClicked = MutableLiveData<Boolean>()
    var onInviteClicked = MutableLiveData<Boolean>()
    var emptyContactListError = MutableLiveData<Boolean>()
    var selectedContactList = ObservableArrayList<ContactEntity>()
    var onSelectClicked = MutableLiveData<Boolean>()
    var inviteMember= MutableLiveData<Boolean>()

    /*
* This method is used to get all the contacts
* */
    fun getAllContacts() {
        contactRepository.getContactsFromDatabase().let {
            try {
                progressDialog.value = false
                val sortedList =
                    contactRepository.getContactsFromDatabase() as MutableList<ContactEntity>
                if (!sortedList.isNullOrEmpty()) {
                    contactAdapter.setList(sortedList)
                    contactAdapter.newContactList?.addAll(sortedList)
                } else {
                    emptyContactListError.value = true
                }
            } catch (e: Exception) {
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
            it.firstName!!.contains(s, ignoreCase = true) || it.contactNumber?.contains(s)!!
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
    }


    /*
* This method is used to call contact sync API
* */ fun callContactSyncApi() {
        val contactRequestDetailsList = mutableListOf<ContactRequestDetails>()
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
                if (responseData is ContactResponse) {
                    // it update the sync status of the contacts which are synced to server and also update the is app user status based on server response
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
                        isApiError.set(false)
                        contactAdapter.newSearchList?.add(contactEntity)
                        contactAdapter.setList(contactAdapter.newSearchList)


                    } else {
                        onIsAppUserClicked.value = true
                    }
                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        progressDialog.value = false
        when (purpose) {
            ApiConstant.API_CHECK_IS_APP_USER -> {
                if (countCheckIsAppUserApiCall == 1) {
                    callIsAppUserApi()
                } else {
                    Utility.showToast("Please try again")
                }


            }

            ApiConstant.API_SNC_CONTACTS -> {
                isApiError.set(true)
                getAllContacts()
            }
        }
    }
/*
* This method is used to check if the user is a app user or not
* */

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


}
