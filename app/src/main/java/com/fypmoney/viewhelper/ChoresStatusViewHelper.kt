package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.ChoresTimeLineItem
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility

class ChoresStatusViewHelper(
    var position: Int,
    var choresStatus: ChoresTimeLineItem?,
    var size: Int?, var nextIsDone: String?
) {
    var date = ObservableField<String?>()
    var name = ObservableField<String?>()
    var status = ObservableField<String?>()
    var statusForImage = ObservableField<String?>()
    var isLineVisible = ObservableField<Boolean?>()
    var isDone = ObservableField<String?>()
    var greenTick = ObservableField<Boolean>()

    init {
        isDone.set(choresStatus?.isDone)
        if (position == size!! - 1) {
            isLineVisible.set(false)
        } else {
            isLineVisible.set(true)

        }
        if (choresStatus?.isDone == "YES") {
            greenTick.set(true)
        } else {
            greenTick.set(false)

        }
        name.set(choresStatus?.name)
        statusForImage.set(choresStatus?.name)

        if (!choresStatus?.date.isNullOrEmpty()) {
            date.set(
                Utility.parseDateTime(
                    choresStatus?.date,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT2,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT4
                )
            )
        }
    }

}