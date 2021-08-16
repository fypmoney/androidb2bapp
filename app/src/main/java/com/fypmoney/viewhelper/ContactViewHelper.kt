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
        if (isAppUser.get() == true) {
            viewModel.onItemClicked.value = contactEntity

        } else {
            onIsAppUserClicked()

        }
    }



    /*
    * This method will handle the click of invite option
    * */
    fun onIsAppUserClicked() {
        viewModel.onIsAppUserClicked.value = true

    }

}