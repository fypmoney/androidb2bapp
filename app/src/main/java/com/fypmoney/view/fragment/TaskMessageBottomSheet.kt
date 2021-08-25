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
import com.fypmoney.databinding.BottomSheetTaskMessageBinding
import com.fypmoney.model.TaskDetailResponse
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.interfaces.MessageSubmitClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.bottom_sheet_task_message.view.*


class TaskMessageBottomSheet(
    var onClickListener: MessageSubmitClickListener,
    var model: TaskDetailResponse,
    var entityId: Int?
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
            R.layout.bottom_sheet_task_message,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetTaskMessageBinding>(
            layoutInflater,
            R.layout.bottom_sheet_task_message,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val btnOtp = view.findViewById<MaterialButton>(R.id.continuebtn)!!
//        if(status=="ACCEPTED"){
//            btnOtp.text = "Continue"
//        }else if(status=="CANCEL"){
//            btnOtp.text = "Withdrawal"
//        }
        btnOtp.text = model.actionAllowed
        view.task_details.text = model.description
        view.card_title.text = model.name
        btnOtp.setOnClickListener(View.OnClickListener {
            if (btnOtp.text == "CANCEL") {
                ChoresActivity.mViewModel!!.callTaskAccept(
                    model.actionAllowed!!,
                    model.entityId.toString(),
                    ""
                )
            }

        })

        if (model?.actionAllowed == "ACCEPTED") {
            btnOtp.text = "Continue"
        }


        return view
    }


}