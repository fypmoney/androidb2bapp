package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.PackageStatusList
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility

class OrderStatusViewHelper(var orderStatus: PackageStatusList?) {
    var date = ObservableField<String>()
    var status = ObservableField<String>()

    init {
        status.set(orderStatus?.status?.replace("_", " "))
        if (!orderStatus?.date.isNullOrEmpty()) {
            date.set(
                Utility.parseDateTime(
                    orderStatus?.date,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT2,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT4
                )
            )
        }
    }

}