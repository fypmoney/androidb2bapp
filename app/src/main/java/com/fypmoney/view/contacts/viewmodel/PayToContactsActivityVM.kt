package com.fypmoney.view.contacts.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
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
import com.fypmoney.view.adapter.ContactListAdapter
import com.fypmoney.view.contacts.adapter.ContactsUiModel
import com.fypmoney.view.contacts.adapter.ContactsUiModel.Companion.contactEntityToContactUiModel

/*
* This is used to handle contacts
* */
class PayToContactsActivityVM(application: Application) : BaseViewModel(application) {
    var userId = SharedPrefUtils.getLong(
        application,
        SharedPrefUtils.SF_KEY_USER_ID
    )
    var contactRepository = ContactRepository(mDB = appDatabase)


    val state:LiveData<PayToContactsState>
        get() = _state
    private val _state = MutableLiveData<PayToContactsState>()

    init {
        callGetWalletBalanceApi()
    }

    /*
* This method is used to get all the contacts
* */
    private fun getAllContacts() {
        contactRepository.getContactsFromDatabase().let {
            val sortedList = contactRepository.getContactsFromDatabase()
            if (!sortedList.isNullOrEmpty()) {
                _state.value = PayToContactsState.SuccessContacts(sortedList.map {
                    contactEntityToContactUiModel(it)
                })
            } else {
                _state.value = PayToContactsState.ContactsIsEmpty
            }
        }
    }

    fun callContactSyncApi() {
        _state.value = PayToContactsState.Loading
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


    /*
     * This method is used to get the balance of wallet
     * */
    private fun callGetWalletBalanceApi() {
        contactRepository.getContactsFromPhoneBookAndStoreInRoom(contentResolver = PockketApplication.instance.contentResolver)
        _state.postValue(PayToContactsState.BalanceLoading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_WALLET_BALANCE,
                NetworkUtil.endURL(ApiConstant.API_GET_WALLET_BALANCE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
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

                }
            }
            ApiConstant.API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    _state.value = PayToContactsState.SuccessBalanceState(Utility.convertToRs(responseData.getWalletBalanceResponseDetails.accountBalance))
                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        progressDialog.value = false
        when (purpose) {
            ApiConstant.API_CHECK_IS_APP_USER -> {
            }

            ApiConstant.API_SNC_CONTACTS -> {
            }

            ApiConstant.API_GET_WALLET_BALANCE -> {
                _state.value = PayToContactsState.BalanceError

            }
        }
    }

    fun callIsAppUserApi(searchedContact:String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_CHECK_IS_APP_USER,
                endpoint = NetworkUtil.endURL(
                    ApiConstant.API_CHECK_IS_APP_USER + searchedContact.trim()
                ),
                request_type = ApiUrl.GET,
                onResponse = this,
                isProgressBar = true,
                param = BaseRequest()
            )
        )
    }

    sealed class PayToContactsState{
        object Loading: PayToContactsState()
        object BalanceLoading: PayToContactsState()
        object BalanceError: PayToContactsState()
        object Error: PayToContactsState()
        object ContactsIsEmpty: PayToContactsState()
        data class SuccessBalanceState(var balance: String?): PayToContactsState()
        data class SuccessContacts(val contacts:List<ContactsUiModel>):PayToContactsState()
    }
    sealed class PayToContactsEvent{

    }
}
