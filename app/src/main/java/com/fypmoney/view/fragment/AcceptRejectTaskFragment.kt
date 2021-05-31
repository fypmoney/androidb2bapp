package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.databinding.BottomSheetAcceptRejectBinding
import com.fypmoney.databinding.BottomSheetLeaveMemberBinding
import com.fypmoney.util.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_leave_member.*

/*
* This is used to leave or remove any member
* */
class AcceptRejectTaskFragment(
    private var taskEntity: TaskEntity?, private var onBottomSheetClickListener: OnBottomSheetClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_accept_reject,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetAcceptRejectBinding>(
            layoutInflater,
            R.layout.bottom_sheet_accept_reject,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val task = view.findViewById<TextView>(R.id.task)!!
        val task_detail = view.findViewById<TextView>(R.id.task_detail)!!
        val relation = view.findViewById<TextView>(R.id.relation)!!
        val amount = view.findViewById<TextView>(R.id.amount)!!
        val time = view.findViewById<TextView>(R.id.time)!!
        val button_reject = view.findViewById<Button>(R.id.button_reject)!!
        val button_accept = view.findViewById<Button>(R.id.button_accept)!!

        button_reject.setOnClickListener {
            dialog!!.cancel()
        }
        button_accept.setOnClickListener {
            onBottomSheetClickListener.onBottomSheetButtonClick()
            dialog!!.cancel()
        }


        return view
    }

    interface OnBottomSheetClickListener {
        fun onBottomSheetButtonClick()

    }

}