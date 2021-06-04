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
import com.fypmoney.model.ContactRequest
import com.fypmoney.model.ContactRequestDetails
import com.fypmoney.model.ContactResponse
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
    var contactRepository = ContactRepository(mDB = appDatabase)
    var onItemClicked = MutableLiveData<ContactEntity>()
    var onIsAppUserClicked = MutableLiveData<Boolean>()
    var emptyContactListError = MutableLiveData<Boolean>()
    var selectedContactList = ObservableArrayList<ContactEntity>()
    var onSelectClicked = MutableLiveData<Boolean>()

    /*
* This method is used to get all the contacts
* */
    private fun getAllContacts() {
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
        val list = contactAdapter.newContactList?.filter {
            it.firstName!!.contains(s, ignoreCase = true) || it.contactNumber?.contains(s)!!
        }
        contactAdapter.setList(list!!)
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


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        progressDialog.value = false
        emptyContactListError.value = true
    }

}
