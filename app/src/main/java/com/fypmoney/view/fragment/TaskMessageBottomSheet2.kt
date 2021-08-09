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
import com.fypmoney.model.UpdateTaskGetResponse
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.interfaces.MessageSubmitClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.bottom_sheet_task_message.view.*


class TaskMessageBottomSheet2(
    var onClickListener: MessageSubmitClickListener,
    var model: UpdateTaskGetResponse?,
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
        Log.d("chackbottomsheet2", model.toString())


        val btnOtp = view.findViewById<MaterialButton>(R.id.continuebtn)!!



        if (model?.currentState == "REJECT") {
            view.card_title.text = "Task Rejected!"
            btnOtp.text = "Continue"
            view.task_details.text =
                "You have rejected the task assigned by ${model?.requesterName}"

            view.message_image.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_task_rejected
                )
            )
        } else if (model?.currentState == "ACCEPT") {
            view.card_title.text = "Task Accepted"
            btnOtp.text = "Continue"
            view.task_details.text =
                "You have accepted the task assigned by ${model?.requesterName}. All the best!"
            view.message_image.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_accepted
                )
            )

        } else if (model?.currentState == "COMPLETE") {
            view.card_title.text = "Good job!"
            view.task_details.text =
                "You have successfully completed your task. Stay tuned you will get your cash soon."

            btnOtp.text = "Continue"
            view.message_image.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_task_completed_graphic
                )
            )

        } else if (model?.currentState == "CANCEL") {
            btnOtp.text = "Continue"
            view.card_title.text = "Task Withdrawn!"
            view.message_image.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_task_withdraw
                )
            )

            view.task_details.text = "You have withdrawn your task."
        } else if (model?.currentState == "DEPRECIATE") {
            btnOtp.text = "Continue"
            view.card_title.text = "Task Depreciated"
            view.message_image.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_whoops_depriciated
                )
            )

            view.task_details.text = "Task has been depreciated"
        } else if (model?.currentState == "APPRECIATEANDPAY") {
            btnOtp.text = "Continue"
            btnOtp.visibility = View.GONE
            view.card_title.text = "Yay!"
            view.message_image.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.task_appretiate
                )
            )
            view.amount.visibility = View.VISIBLE
            var amount = model?.amount!! / 100
            view.amount.text = "₹" + amount

            view.task_details.text =
                "Hey Champ you have done a great job! I am proud of you. \uD83D\uDC4F"
        }



        btnOtp.setOnClickListener(View.OnClickListener {
            if (btnOtp.text == "WITHDRAWAL") {
                ChoresActivity.mViewModel!!.callTaskAccept("CANCEL", model?.id.toString(), "")
            } else if (btnOtp.text == "Continue") {
                onClickListener.onSubmit()

            }

        })




        return view
    }


}