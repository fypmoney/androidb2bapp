package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.SampleTaskModel
import com.fypmoney.view.adapter.CreateTaskAdapter

/*
* This is used to display all the notifications in the list
* */
class CreateTaskViewHelper(
    var size: Int,
    var position: Int,
    var notification: SampleTaskModel.SampleTaskDetails?,
    var onNotificationClickListener: CreateTaskAdapter.OnSampleTaskClickListener
) {
    var actionAllowed = ObservableField<String>()
    var isDividerVisible = ObservableField(true)

    fun init() {
        if (position == size - 1) {
            isDividerVisible.set(false)
        }

    }

    fun onItemClicked() {
        onNotificationClickListener.onNotificationClick(notification, position)

    }


}