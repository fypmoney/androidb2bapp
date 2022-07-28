package com.fypmoney.view.insights.view

import android.app.Application
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.insights.model.AllTxnItem

class CategoryWaiseTransactionDetailsFragmentVM(application: Application) : BaseViewModel(application) {
    lateinit var txnItem: AllTxnItem

    val state:LiveData<CategoryWaiseTxnDetailsState>
        get() = _state
    private val _state = MutableLiveData<CategoryWaiseTxnDetailsState>()

    val event:LiveData<CategoryWaiseTxnDetailsEvent>
        get() = _event
    private val _event = LiveEvent<CategoryWaiseTxnDetailsEvent>()

    fun onGetHelpClick(){
        _event.value = CategoryWaiseTxnDetailsEvent.ShowHelp
    }
    fun showData(){
        val categoryTxnDetailsUiModel = CategoryTxnDetailsUiModel(
            txnCategoryImage = txnItem.iconLink,
            txnName = txnItem.userName,
            txnUserMobileNumber = txnItem.mobileNo,
            txnAmount = String.format(PockketApplication.instance.getString(R.string.amount_with_currency),Utility.convertToRs(txnItem.amount)),
            txnStatusDateAndTime = if(txnItem.transactionType=="Debited") String.format(PockketApplication.instance.getString(R.string.sent_with_time),
                Utility.parseDateTimeWithPlusFiveThirty(txnItem.transactionDate?.replace("+05:30","Z"),
                AppConstants.SERVER_DATE_TIME_FORMAT3,
                AppConstants.CHANGED_DATE_TIME_FORMAT3
            ),) else if(txnItem.transactionType.equals("Credited",false)) String.format(PockketApplication.instance.getString(R.string.reccived_with_time),
                Utility.parseDateTimeWithPlusFiveThirty(txnItem.transactionDate?.replace("+05:30","Z"),
                    AppConstants.SERVER_DATE_TIME_FORMAT3,
                    AppConstants.CHANGED_DATE_TIME_FORMAT3
                ),)
                    else{
                        ""
            },
            txnCategory = txnItem.category,
            txnFypTxnId = txnItem.accReferenceNumber,
            bankTxnId = txnItem.bankReferenceNumber
        )
        _state.value = CategoryWaiseTxnDetailsState.ShowTxnDetails(categoryTxnDetailsUiModel)
    }
    sealed class CategoryWaiseTxnDetailsState{
        data class ShowTxnDetails(var categoryTxnDetailsUiModel: CategoryTxnDetailsUiModel):CategoryWaiseTxnDetailsState()
    }

    sealed class CategoryWaiseTxnDetailsEvent{
        object ShowHelp:CategoryWaiseTxnDetailsEvent()
    }

    @Keep
    data class CategoryTxnDetailsUiModel(
        var txnCategoryImage:String?,
        var txnName:String?,
        var txnUserMobileNumber:String?,
        var txnAmount:String?,
        var txnStatusDateAndTime:String?,
        var txnCategory:String?,
        var txnFypTxnId:String?,
        var bankTxnId:String?
    )
}