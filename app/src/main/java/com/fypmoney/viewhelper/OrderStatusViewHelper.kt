package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.PackageStatusList
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility

class OrderStatusViewHelper(
    var position: Int,
    var orderStatus: PackageStatusList?,
    var size: Int?,var nextIsDone:String?
) {
    var date = ObservableField<String?>()
    var status = ObservableField<String?>()
    var statusForImage = ObservableField<String?>()
    var isLineVisible = ObservableField<Boolean?>()
    var isDone = ObservableField<String?>()

    init {
        isDone.set(orderStatus?.isDone)
        if (position == size!! - 1) {
            isLineVisible.set(false)
        } else {
            isLineVisible.set(true)

        }
        status.set(orderStatus?.status?.replace("_", " "))
        statusForImage.set(orderStatus?.status)

        if (!orderStatus?.date.isNullOrEmpty()) {
            date.set(
                Utility.parseDateTime(
                    orderStatus?.date,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT2,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT3
                )
            )
        }
    }

}