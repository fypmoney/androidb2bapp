package com.fypmoney.view.contacts.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fypmoney.R
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.GetWalletBalanceResponse
import com.fypmoney.model.IsAppUserResponse
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.contacts.adapter.ContactsUiModel
import com.fypmoney.view.contacts.model.ContactsActivityUiModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/*
* This is used to handle contacts
* */
@FlowPreview
@ObsoleteCoroutinesApi
class PayToContactsActivityVM(application: Application) : BaseViewModel(application) {
    val state:LiveData<PayToContactsState>
        get() = _state
    private val _state = MediatorLiveData<PayToContactsState>()

    val event:LiveData<PayToContactsEvent>
        get() = _event
    private val _event = LiveEvent<PayToContactsEvent>()

    val searchContact = MutableLiveData<String>()
    var contactList = mutableListOf<ContactsUiModel>()
    lateinit var selectedContacts:ContactsUiModel
    lateinit var contactsActivityUiModel: ContactsActivityUiModel

    @ObsoleteCoroutinesApi
    private val searchContactBroadcastChannel = ConflatedBroadcastChannel<String>()


    init {
        callGetWalletBalanceApi()
        emitSearchQuery()
        observeSearchQuery()
    }

    @ObsoleteCoroutinesApi
    private fun emitSearchQuery() {
        _state.addSource(searchContact) {
            it?.let { searchContactBroadcastChannel.trySend(it) }
        }
    }

    @FlowPreview
    private fun observeSearchQuery(){
        viewModelScope.launch {
            searchContactBroadcastChannel.asFlow().debounce(800).collect {
                if(contactList.isNotEmpty()){
                    val filteredList = contactList.filterIndexed { index, contact ->
                        contact.firstAndLastName.contains(it,true) || contact.mobileNumber.contains(it,true)
                    }
                    if(filteredList.isEmpty()){
                        val contactList = mutableListOf<ContactsUiModel>()
                        contactList.add(ContactsUiModel(firstAndLastName = "No Name",
                            mobileNumber = it,   imageDrawable = R.drawable.ic_contact_user_1))
                        _state.value = PayToContactsState.SuccessContacts(contactList)
                    }else{
                        _state.value = PayToContactsState.SuccessContacts(filteredList)
                    }
                }/*else{
                    val contactList = mutableListOf<ContactsUiModel>()
                    contactList.add(ContactsUiModel(firstAndLastName = "No Name",
                        mobileNumber = it,   imageDrawable = R.drawable.ic_contact_user_1))
                    _state.value = PayToContactsState.SuccessContacts(contactList)
                }*/
            }
        }
    }

    fun checkNeedToShowBalanceOnScreen(){
        if(contactsActivityUiModel.showLoadingBalance){
            _state.value = PayToContactsState.ShowBalanceView
            callGetWalletBalanceApi()
        }else{
            _state.value = PayToContactsState.HideBalanceView
        }
    }

    fun fetchContactShow(contentResolver: ContentResolver){
        contactList = getContactNumberFromPhoneBook(contentResolver)
        if(contactList.isEmpty()){
            _state.value = PayToContactsState.ContactsIsEmpty
        }else{
            _state.value = PayToContactsState.SuccessContacts(contactList)
        }
    }

    private fun getContactNumberFromPhoneBook(contentResolver: ContentResolver): MutableList<ContactsUiModel> {
        val contactList = mutableListOf<ContactsUiModel>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
        )
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
        } catch (e: SecurityException) {
            //SecurityException can be thrown if we don't have the right permissions
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        if (cursor != null) {
            try {
                val normalizedNumbersAlreadyFound: HashSet<String> = HashSet()
                val indexOfNormalizedNumber =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                val indexOfDisplayName =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val indexOfDisplayNumber =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (cursor.moveToNext()) {
                    val normalizedNumber = cursor.getString(indexOfNormalizedNumber)
                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                        val displayName = cursor.getString(indexOfDisplayName)
                        val displayNumber = cursor.getString(indexOfDisplayNumber)
                        //haven't seen this number yet: do something with this contact!
                        if(displayNumber.length>=10){
                            var updatedNumber = displayNumber.replace(" ", "").trim()
                            if (updatedNumber.length > 10) {
                                updatedNumber = updatedNumber.takeLast(10)
                            }
                            val contact = ContactsUiModel(
                                firstAndLastName = displayName,
                                mobileNumber = updatedNumber,
                                imageDrawable = getRandomContactImage()
                            )
                            contactList.add(contact)
                        }
                    } else {
                        //don't do anything with this contact because we've already found this number
                    }
                }
            }catch (e:Exception){
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                cursor.close()
            }
        }
        return contactList
    }
    private fun getRandomContactImage():Int{
        val randomImages = arrayOf(
            R.drawable.ic_contact_user_1,
            R.drawable.ic_contact_user_2,
            R.drawable.ic_contact_user_3,
            R.drawable.ic_contact_user_4,
            R.drawable.ic_contact_user_5,
        )
         return randomImages[(0..4).random()]
    }

    /*
* This method is used to get all the contacts
* */
/*    private fun getAllContacts() {
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
    }*/

    //TODO Sync Api Removed for now due to server cost optimization
/*
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
*/


    /*
     * This method is used to get the balance of wallet
     * */
    private fun callGetWalletBalanceApi() {
        //contactRepository.getContactsFromPhoneBookAndStoreInRoom(contentResolver = PockketApplication.instance.contentResolver)
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
            /*ApiConstant.API_SNC_CONTACTS -> {
                if (responseData is ContactResponse) {
                    // it update the sync status of the contacts which are synced to server and also update the is app user status based on server response
                    //contactRepository.updateIsSyncAndIsAppUserStatus(responseData.contactResponseDetails?.userPhoneContact)
                    //getAllContacts()
                }
            }*/
            ApiConstant.API_CHECK_IS_APP_USER -> {
                if (responseData is IsAppUserResponse) {
                    if(responseData.isAppUserResponseDetails.isAppUser!!){
                        val contactEntity = ContactEntity()
                        val firstNameLastName = responseData.isAppUserResponseDetails.name?.split(" ")
                        firstNameLastName?.let {
                            if(it.size>1){
                                contactEntity.firstName = it[0]
                                contactEntity.lastName = it[1]
                            }else{
                                contactEntity.firstName = it[0]
                            }
                        }
                        contactEntity.contactNumber = selectedContacts.mobileNumber
                        contactEntity.isAppUser = true
                        contactEntity.profilePicResourceId = responseData.isAppUserResponseDetails.profilePicResourceId
                        contactEntity.userId = responseData.isAppUserResponseDetails.userId
                        _event.value = PayToContactsEvent.SelectedContactUserIsFyper(contactEntity)
                    }else{
                        _event.value = PayToContactsEvent.SelectedContactUserIsNotFyper(selectedContacts)
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
                _event.value = PayToContactsEvent.SelectedContactUserIsNotFyper(selectedContacts)
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
        object HideBalanceView: PayToContactsState()
        object ShowBalanceView: PayToContactsState()
        data class SuccessBalanceState(var balance: String?): PayToContactsState()
        data class SuccessContacts(val contacts:List<ContactsUiModel>):PayToContactsState()
    }
    sealed class PayToContactsEvent{
        data class SelectedContactUserIsFyper(val contactEntity: ContactEntity):PayToContactsEvent()
        data class SelectedContactUserIsNotFyper(val contactsUiModel: ContactsUiModel):PayToContactsEvent()
    }
}
