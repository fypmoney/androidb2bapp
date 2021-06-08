package com.fypmoney.viewmodel

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.TransactionHistoryResponseDetails
import com.fypmoney.view.adapter.TransactionHistoryAdapter

class TransactionHistoryViewModel(application: Application) : BaseViewModel(application) {
    var onPayOrRequestClicked = MutableLiveData<View>()
    var contactResult = ObservableField(ContactEntity())
    var transactionHistoryAdapter = TransactionHistoryAdapter(this)
    var contactName = ObservableField<String>()

    init {
        val transList = ArrayList<TransactionHistoryResponseDetails>()

        for (i in 1..10) {
            val transactionHistoryResponseDetails = TransactionHistoryResponseDetails()
            transactionHistoryResponseDetails.amount = "200"
            transactionHistoryResponseDetails.message = "you pay, 5 aug"
            transList.add(transactionHistoryResponseDetails)
        }

        transactionHistoryAdapter.setList(transList)


    }
    /*
  * This is used to handle pay or request button click
  * */

    fun onPayOrRequestClicked(view: View) {
        onPayOrRequestClicked.value = view

    }

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
}