package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.ContactAdapter
import com.fypmoney.viewmodel.ContactViewModel
import java.lang.Exception


/*
* This is used to display all the contacts in the list
* */
class ContactViewHelper(
    var contactAdapter: ContactAdapter,
    var position: Int? = -1,
    var contactEntity: ContactEntity?,
    var viewModel: ContactViewModel
) {

    var isContactSelected = ObservableField(false)
    var isAppUser = ObservableField(false)
    var isBackgroundHighlight = ObservableField<Boolean>()

    fun init() {

        when (contactEntity?.isAppUser) {
            true -> {
                isAppUser.set(true)
            }
            else -> {
                isAppUser.set(false)
            }

        }
    }

    /*
     * called when any item is selected
     * */
    fun onItemClicked() {
        if (viewModel.selectedContactList.size < 1) {
            contactEntity?.isSelected = contactEntity?.isSelected != true
            viewModel.selectedContactList.add(contactEntity!!)
            isBackgroundHighlight.set(true)
        } else if (viewModel.selectedContactList.size == 1) {
            if (viewModel.selectedContactList[0].contactNumber == contactEntity?.contactNumber) {
                contactEntity?.isSelected = contactEntity?.isSelected != true
                isBackgroundHighlight.set(false)
                contactEntity?.let {
                    viewModel.selectedContactList.remove(it)

                }
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
        viewModel.onIsAppUserClicked.value = true

    }

}