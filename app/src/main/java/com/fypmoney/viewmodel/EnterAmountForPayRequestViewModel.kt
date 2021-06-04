package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility

class EnterAmountForPayRequestViewModel(application: Application) : BaseViewModel(application) {
    var contactName = ObservableField<String>()
    var onPayClicked = MutableLiveData(false)
    var setEdittextLength = MutableLiveData(false)
    var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
    var amountSelected = ObservableField<String>()
    var action = ObservableField<String>()
    var message = ObservableField<String>()
    var contactResult = ObservableField(ContactEntity())
    var buttonText = ObservableField(application.getString(R.string.pay_btn_text))

    /*
      * This method is used to handle on click of pay or request button
      * */
    fun onPayClicked() {
        when {
            TextUtils.isEmpty(amountSelected.get()) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.add_money_empty_error))
            }
            else -> {
                onPayClicked.value = true
            }
        }
    }

    fun onAmountSelected(amount: Int) {
        amountSelected.set(amount.toString())
        setEdittextLength.value = true
    }

    /*
   * This is used to set selected response
   * */
    fun setResponseAfterContactSelected(contactEntity: ContactEntity?, actionValue: String?) {
        try {
            if (contactEntity?.contactNumber != null) {
                contactResult.set(contactEntity)
                if (action.get() != AppConstants.PAY) {
                    buttonText.set(PockketApplication.instance.getString(R.string.request_btn_text))
                }
                action.set(actionValue)
                if (contactResult.get()?.lastName.isNullOrEmpty()) {
                    if (action.get() == AppConstants.PAY)
                        contactName.set(PockketApplication.instance.getString(R.string.pay_btn_text) + " " + contactEntity.firstName)
                    else {
                        contactName.set(PockketApplication.instance.getString(R.string.request_from) + " " + contactEntity.firstName)

                    }
                } else {
                    if (action.get() == AppConstants.PAY)
                        contactName.set(PockketApplication.instance.getString(R.string.pay_btn_text) + " " + contactEntity.firstName + " " + contactEntity.lastName)
                    else {
                        contactName.set(PockketApplication.instance.getString(R.string.request_from) + " " + contactEntity.firstName + " " + contactEntity.lastName)

                    }


                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}