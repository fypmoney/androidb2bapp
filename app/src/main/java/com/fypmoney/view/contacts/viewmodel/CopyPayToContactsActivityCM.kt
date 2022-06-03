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
class CopyPayToContactsActivityCM(application: Application) : BaseViewModel(application) {
    var userId = SharedPrefUtils.getLong(
        application,
        SharedPrefUtils.SF_KEY_USER_ID
    )
    var contactAdapter = ContactListAdapter(this@CopyPayToContactsActivityCM, userId)
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
    var contactNotFound = MutableLiveData(false)
    var availableAmount =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))

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
            it.firstName?.contains(
                s,
                ignoreCase = true
            ) == true || it.contactNumber?.contains(s) == true
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
* */
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

            ApiConstant.API_GET_WALLET_BALANCE -> {
                _state.value = PayToContactsState.BalanceError

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
