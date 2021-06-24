package com.fypmoney.viewhelper

import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.model.AddNewCardDetails
import com.fypmoney.model.SavedCardResponseDetails
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.SavedCardsAdapter
import com.fypmoney.viewmodel.AddMoneyUpiDebitViewModel

class SavedCardViewHelper(
    var position: Int,
    var savedCard: SavedCardResponseDetails?,
    var viewModel: AddMoneyUpiDebitViewModel, var savedCardsAdapter: SavedCardsAdapter
) {
    var amount = ObservableField<String>()
    var cvv = MutableLiveData<String>()

    init {
        amount.set(PockketApplication.instance.getString(R.string.Rs) + viewModel.amountToAdd.get())
    }

    /*
    * This is used to handle the radio buton click
    * */

    fun onRadioClicked() {
        savedCardsAdapter.cardList?.forEach {
            if (it.isSelected == true) {
                it.isSelected = false
            }

        }
        savedCardsAdapter.notifyDataSetChanged()
        savedCard?.isSelected = true
    }

    /*
    * This method is used to handle add amount in saved cards
    * */
    fun onAddAmountClicked() {
        when {
            TextUtils.isEmpty(cvv.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.card_cvv_valid_error))
            }
            cvv.value?.length!! < 3 -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.card_cvv_valid_error))
            }
            else -> {
                viewModel.onAddInSaveCardClicked.value = AddNewCardDetails(
                    card_token = savedCard!!.card_token,
                    nameOnCard = savedCard!!.name_on_card,
                    expiryMonth = savedCard!!.expiry_month,
                    expiryYear = savedCard!!.expiry_year,
                    isCardSaved = true,
                    cvv = cvv.value!!
                )

            }
        }
    }


}