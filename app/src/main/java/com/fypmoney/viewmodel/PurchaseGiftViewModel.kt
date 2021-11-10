package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
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
import com.fypmoney.view.giftCardModule.ContactsGiftCardAdapter
import com.fypmoney.view.giftCardModule.model.GiftProductResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used to handle add money functionality
* */
class PurchaseGiftViewModel(application: Application) : BaseViewModel(application) {
    var onAddClicked = MutableLiveData(false)
    var userId = SharedPrefUtils.getLong(
        application,
        SharedPrefUtils.SF_KEY_USER_ID
    )
    var searchedContact = ObservableField<String>()
    var productList = MutableLiveData<GiftProductResponse>()
    var emptyContactListError = MutableLiveData<Boolean>()
    var contactNotFound = MutableLiveData<Boolean>(false)
    var onItemClicked = MutableLiveData<ContactEntity>()
    var contactRepository = ContactRepository(mDB = appDatabase)
    var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
    var amountSelected = ObservableField<String>()
    var isFetchBalanceVisible = ObservableField(true)
    var contactAdapter = ContactsGiftCardAdapter(this, userId)
    var onIsAppUserClicked = MutableLiveData<Boolean>()
    var selectedContactList = ObservableArrayList<ContactEntity>()
    var countCheckIsAppUserApiCall: Int? = 0
    var onSelectClicked = MutableLiveData<Boolean>()
    fun callBrandGiftCards(orderId: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.GET_BRAND_GIFT_CARD,
                NetworkUtil.endURL(ApiConstant.GET_BRAND_GIFT_CARD + orderId),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )


    }

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

    fun onQueryTextChange(s: String) {
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

    fun callContactSyncApi() {

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


    fun onAddClicked() {
//        amountSelected.get()?.toIntOrNull()?.let {
//            when {
//                TextUtils.isEmpty(amountSelected.get()) -> {
//                    Utility.showToast(PockketApplication.instance.getString(R.string.add_money_empty_error))
//                }
//                it < 10 -> {
//                    Utility.showToast(PockketApplication.instance.getString(R.string.minimum_load_amount))
//                }
//                else -> {
//                    onAddClicked.value = true
//                }
//            }
//        }


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
            ApiConstant.GET_BRAND_GIFT_CARD -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val list = Gson().fromJson(
                    json.get("data"),
                    GiftProductResponse::class.java
                )

                productList.postValue(list)
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

    private fun getAllContacts() {

        contactRepository.getContactsFromDatabase().let {


            progressDialog.value = false
            val sortedList =
                contactRepository.getContactsFromDatabase() as MutableList<ContactEntity>
            if (!sortedList.isNullOrEmpty()) {

                contactAdapter.setList(sortedList)
                contactAdapter.newContactList?.addAll(sortedList)
            } else {

                contactNotFound.value = true
            }
//            } catch (e: Exception) {
//
//                e.printStackTrace()
//            }


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


