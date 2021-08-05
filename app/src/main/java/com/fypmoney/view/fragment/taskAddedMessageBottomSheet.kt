package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetTaskAddedMessageBinding
import com.fypmoney.databinding.BottomSheetTaskMessageBinding
import com.fypmoney.model.UpdateTaskGetResponse
import com.fypmoney.view.interfaces.MessageSubmitClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.bottom_sheet_task_added_message.view.*

import kotlinx.android.synthetic.main.bottom_sheet_task_message.view.*
import kotlinx.android.synthetic.main.bottom_sheet_task_message.view.continuebtn
import kotlinx.android.synthetic.main.bottom_sheet_task_message.view.task_details


class taskAddedMessageBottomSheet(
    var onClickListener: MessageSubmitClickListener,
    var list: UpdateTaskGetResponse,

    ) :
    BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    var otp = ObservableField<String>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_task_added_message,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetTaskAddedMessageBinding>(
            layoutInflater,
            R.layout.bottom_sheet_task_added_message,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)



        view.message.text = list.msg
        view.continuebtn.setOnClickListener(View.OnClickListener {
            onClickListener.onSubmit()

        })




        return view
    }


}