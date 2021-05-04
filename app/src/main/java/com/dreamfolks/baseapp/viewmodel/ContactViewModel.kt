package com.dreamfolks.baseapp.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.dreamfolks.baseapp.base.BaseViewModel
import com.dreamfolks.baseapp.connectivity.ApiConstant
import com.dreamfolks.baseapp.connectivity.ApiUrl
import com.dreamfolks.baseapp.connectivity.ErrorResponseInfo
import com.dreamfolks.baseapp.connectivity.network.NetworkUtil
import com.dreamfolks.baseapp.connectivity.retrofit.ApiRequest
import com.dreamfolks.baseapp.connectivity.retrofit.WebApiCaller
import com.dreamfolks.baseapp.database.ContactRepository
import com.dreamfolks.baseapp.database.entity.ContactEntity
import com.dreamfolks.baseapp.model.ContactRequest
import com.dreamfolks.baseapp.model.ContactRequestDetails
import com.dreamfolks.baseapp.model.ContactResponse
import com.dreamfolks.baseapp.util.SharedPrefUtils
import com.dreamfolks.baseapp.util.Utility
import com.dreamfolks.baseapp.view.adapter.ContactAdapter
import java.lang.Exception

/*
* This is used to handle contacts
* */
class ContactViewModel(application: Application) : BaseViewModel(application) {
    var contactAdapter = ContactAdapter(this)
    var isClickable = ObservableField(false)
    var contactRepository = ContactRepository(mDB = appDatabase)
    var selectedContactList = ArrayList<ContactEntity>()
    var onContinueClicked = MutableLiveData<Boolean>()
    var onIsAppUserClicked = MutableLiveData<Boolean>()
    var emptyContactListError = MutableLiveData<Boolean>()
    var selectedPosition = ObservableField(-1)

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
                    contactAdapter.newContactList?.addAll(sortedList as MutableList<ContactEntity>)
                } else {
                    emptyContactListError.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }


    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val list = contactAdapter.newContactList?.filter {
            it.firstName!!.contains(s, ignoreCase = true) || it.contactNumber?.contains(s)!!
        }
        contactAdapter.setList(list!!)
    }

    fun onContinueClicked() {
        onContinueClicked.value = true

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
