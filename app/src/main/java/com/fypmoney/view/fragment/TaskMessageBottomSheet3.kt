package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetTaskMessageBinding
import com.fypmoney.model.NotificationModel
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.interfaces.MessageSubmitClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.bottom_sheet_task_message.view.*


class TaskMessageBottomSheet3(
    var onClickListener: MessageSubmitClickListener,
    var model: NotificationModel.NotificationResponseDetails?,
    var entityId: String?
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
        Log.d("chackbottomsheet3", model.toString())

        val btnOtp = view.findViewById<MaterialButton>(R.id.continuebtn)!!
//        if(status=="ACCEPTED"){
//            btnOtp.text = "Continue"
//        }else if(status=="CANCEL"){
//            btnOtp.text = "Withdrawal"
//        }

        if (model?.actionAllowed == "CANCEL") {
            btnOtp.text = "Cancel"

        } else if (model?.actionAllowed == "" && model?.requestStatus == "DEPRECIATE") {
            view.card_title.text = "Oops!"
            view.task_details.visibility =
                View.VISIBLE
            btnOtp.text = "Continue"
            view.message_image.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_task_depreciate
                )
            )
            view.task_details.text =
                "We are sorry buddy! Your task has been Depreciated by ${model?.destinationUserName}"

        }

        btnOtp.setOnClickListener(View.OnClickListener {
            if (btnOtp.text == "WITHDRAWAL") {
                ChoresActivity.mViewModel!!.callTaskAccept("CANCEL", entityId, "")
            } else if (btnOtp.text == "Continue") {
                onClickListener.onSubmit()
            }

        })




        return view
    }


}