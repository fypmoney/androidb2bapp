package com.fypmoney.viewhelper

import android.util.Log
import androidx.databinding.ObservableField
import com.fypmoney.application.PockketApplication
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.ContactListViewModel


/*
* This is used to display all the contacts in the list
* */
class ContactListViewHelper(
    var position: Int? = -1,
    var contactEntity: ContactEntity?,
    var viewModel: ContactListViewModel
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
        isApiError.set(viewModel.isApiError.get())

    }

    /*
     * called when any item is selected
     * */
    fun onItemClicked() {
        viewModel.searchedContact.set(contactEntity?.contactNumber)
        viewModel.searchedName.set(contactEntity?.firstName)
        when (isApiError.get()) {
            false -> {
                if (isAppUser.get() == true) {
                    viewModel.onItemClicked.value = contactEntity

                } else {
                    onIsAppUserClicked()

                }
            }
            true -> {
                viewModel.callIsAppUserApi()

            }
        }
    }

    /*
 * This method will check the size of selected radio and allow single selection
 * */
    private fun checkSelectedSizeOfContactList() {
        when (viewModel.selectedContactList.size) {
            0, 1 -> {
                isContactSelected.set(true)
                if (viewModel.selectedContactList.isNullOrEmpty())
                    viewModel.selectedContactList.let {
                    }

            }

            else -> {
                isContactSelected.set(false)
                Utility.showToast("Limit reached")
            }
        }

    }

    /*
    * This method will handle the click of invite option
    * */
    fun onIsAppUserClicked() {
        viewModel.onInviteClicked.value = true
    }

}