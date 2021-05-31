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
import com.fypmoney.databinding.BottomSheetLeaveMemberBinding
import com.fypmoney.databinding.BottomYayBindingImpl
import com.fypmoney.util.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_leave_member.*

/*
* This is used to leave or remove any member
* */
class YayBSFragment(
    private var taskEntity: TaskEntity?, private var onBottomSheetClickListener: OnYyBottomSheetClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_yay,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomYayBindingImpl>(
            layoutInflater,
            R.layout.bottom_yay,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        //val task = view.findViewById<TextView>(R.id.task)!!


        /*button_reject.setOnClickListener {
            dialog!!.cancel()
        }
        button_accept.setOnClickListener {
            onBottomSheetClickListener.onBottomSheetButtonClick()
            dialog!!.cancel()
        }*/


        return view
    }

    interface OnYyBottomSheetClickListener {
        fun OnYyBottomSheetClickListener()

    }

}