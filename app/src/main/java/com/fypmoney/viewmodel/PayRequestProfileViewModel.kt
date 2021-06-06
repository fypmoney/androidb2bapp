package com.fypmoney.viewmodel

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.database.entity.ContactEntity

class PayRequestProfileViewModel(application: Application) : BaseViewModel(application) {
    var contactName = ObservableField<String>()
    var contactResult = ObservableField(ContactEntity())
    var onPayOrRequestClicked = MutableLiveData<View>()

    /*
       * This is used to set selected response
       * */
    fun setResponseAfterContactSelected(contactEntity: ContactEntity?) {
        try {
            if (contactEntity?.contactNumber != null) {
                contactResult.set(contactEntity)
                if (contactResult.get()?.lastName.isNullOrEmpty()) {
                    contactName.set(contactEntity.firstName)
                } else {
                    contactName.set(contactEntity.firstName + " " + contactEntity.lastName)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /*
    * This is used to handle pay or request button click
    * */

    fun onPayOrRequestClicked(view: View) {
        onPayOrRequestClicked.value = view

    }
}