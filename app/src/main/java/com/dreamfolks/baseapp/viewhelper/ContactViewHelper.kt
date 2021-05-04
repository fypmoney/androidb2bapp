package com.dreamfolks.baseapp.viewhelper

import androidx.databinding.ObservableField
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.application.PockketApplication
import com.dreamfolks.baseapp.database.entity.ContactEntity
import com.dreamfolks.baseapp.util.Utility
import com.dreamfolks.baseapp.view.adapter.ContactAdapter
import com.dreamfolks.baseapp.viewmodel.ContactViewModel


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
    var isRadioVisible = ObservableField(false)
    fun init() {

        when (contactEntity?.isAppUser) {
            true -> {
                isRadioVisible.set(true)
            }
            else -> {
                isRadioVisible.set(false)
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
            isContactSelected.set(true)
        } else if (viewModel.selectedContactList.size == 1) {
            if (viewModel.selectedContactList[0].contactNumber == contactEntity?.contactNumber) {
                contactEntity?.isSelected = contactEntity?.isSelected != true
                isContactSelected.set(false)
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