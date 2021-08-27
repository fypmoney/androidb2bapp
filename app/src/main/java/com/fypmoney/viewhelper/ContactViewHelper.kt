package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.ContactViewModel


/*
* This is used to display all the contacts in the list
* */
class ContactViewHelper(
    var position: Int? = -1,
    var contactEntity: ContactEntity?,
    var viewModel: ContactViewModel,
    var userId: Long
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
        if (userId.toString() != contactEntity?.userId) {
            if (isAppUser.get() == true) {
                viewModel.onItemClicked.value = contactEntity

            } else {
                onIsAppUserClicked()

            }
        } else {
            Utility.showToast("User cannot select itself")
        }
    }



    /*
    * This method will handle the click of invite option
    * */
    fun onIsAppUserClicked() {
        viewModel.onIsAppUserClicked.value = true

    }

}