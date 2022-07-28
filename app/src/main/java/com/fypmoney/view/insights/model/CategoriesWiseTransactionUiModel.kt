package com.fypmoney.view.insights.model

import android.content.Context
import androidx.annotation.Keep
import com.fypmoney.R
import com.fypmoney.util.AppConstants.CHANGED_DATE_TIME_FORMAT3
import com.fypmoney.util.AppConstants.SERVER_DATE_TIME_FORMAT3
import com.fypmoney.util.Utility


@Keep
data class CategoriesWiseTransactionUiModel(
    var categoryIconLink:String?,
    var categoryTxnTitle:String?,
    var categoryTxnTime:String?,
    var txnAmount:String?
){
    companion object{
        fun mapAllTxnItemToCategoriesWiseTransactionUiModel(context: Context, allTxnItem: AllTxnItem):CategoriesWiseTransactionUiModel{
            return CategoriesWiseTransactionUiModel(
                categoryIconLink = allTxnItem.iconLink,
                categoryTxnTitle = allTxnItem.userName,
                categoryTxnTime = Utility.parseDateTimeWithPlusFiveThirty(allTxnItem.transactionDate?.replace("+05:30","Z"),SERVER_DATE_TIME_FORMAT3,CHANGED_DATE_TIME_FORMAT3),
                txnAmount = String.format(context.getString(R.string.amount_with_currency),Utility.convertToRs(allTxnItem.amount))
            )
        }
    }
}