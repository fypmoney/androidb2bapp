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
) : BottomSheetDialogFragment() {
    private lateinit var binding:BottomSheetTaskMessageBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_task_message,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (model?.currentState == "REJECT") {
            binding.cardTitle.text = "Task Rejected!"
            binding.continuebtn.text = "Continue"
            binding.taskDetails.text =
                "You have rejected the task assigned by ${model?.requesterName}"

            binding.messageImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_task_rejected
                )
            )
        } else if (model?.currentState == "ACCEPT") {
            binding.cardTitle.text = "Task Accepted"
            binding.continuebtn.text = "Continue"
            binding.taskDetails.text =
                "You have accepted the task assigned by ${model?.requesterName}. All the best!"
            binding.messageImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_accepted
                )
            )

        } else if (model?.currentState == "COMPLETE") {
            binding.cardTitle.text = "Good job!"
            binding.taskDetails.text =
                "You have successfully completed your task. Stay tuned you will get your cash soon."

            binding.continuebtn.text = "Continue"
            binding.messageImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_task_completed_graphic
                )
            )

        } else if (model?.currentState == "CANCEL") {
            binding.continuebtn.text = "Continue"
            binding.cardTitle.text = "Task Withdrawn!"
            binding.messageImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_task_withdraw
                )
            )

            binding.taskDetails.text = "You have withdrawn your task."
        } else if (model?.currentState == "DEPRECIATE") {
            binding.continuebtn.text = "Continue"
            binding.cardTitle.text = "Task Depreciated"
            binding.messageImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_whoops_depriciated
                )
            )

            binding.taskDetails.text = "Task has been depreciated"
        } else if (model?.currentState == "APPRECIATEANDPAY") {
            binding.continuebtn.text = "Continue"
            binding.continuebtn.visibility = View.GONE
            binding.cardTitle.text = "Yay!"
            binding.messageImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.task_appretiate
                )
            )
            binding.amount.visibility = View.VISIBLE
            var amount = model?.amount!! / 100
            binding.amount.text = "₹" + amount

            binding.taskDetails.text =
                "Hey Champ you have done a great job! I am proud of you. \uD83D\uDC4F"
        }



        binding.continuebtn.setOnClickListener(View.OnClickListener {
            if (binding.continuebtn.text == "WITHDRAWAL") {
                ChoresActivity.mViewModel!!.callTaskAccept("CANCEL", model?.id.toString(), "")
            } else if (binding.continuebtn.text == "Continue") {
                onClickListener.onSubmit()

            }

        })



    }


}