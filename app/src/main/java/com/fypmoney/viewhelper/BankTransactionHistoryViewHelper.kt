package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.BankTransactionHistoryViewModel
import com.fypmoney.viewmodel.ContactListViewModel


/*
* This is used to display all the contacts in the list
* */
class BankTransactionHistoryViewHelper(
    var position: Int? = -1,
    var contactEntity: ContactEntity?,
    var viewModel: BankTransactionHistoryViewModel
) {

    var isContactSelected = ObservableField(false)
    var isAppUser = ObservableField(false)
    var isBackgroundHighlight = ObservableField<Boolean>()
    var isApiError = ObservableField(false)

    fun init() {
        when (contactEntity?.isAppUser) {
            true -> {
                isAppUser.set(true)
            }
            else -> {
                isAppUser.set(false)
            }

        }
       // isApiError.set(viewModel.isApiError.get())
    }

    /*
     * called when any item is selected
     * */
    fun onItemClicked() {
    }



}