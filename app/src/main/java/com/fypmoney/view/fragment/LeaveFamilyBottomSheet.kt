package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetLeaveFamilyBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/*
* This is used to leave a family
* */
class LeaveFamilyBottomSheet(
    var onLeaveFamilyClickListener: OnLeaveFamilyClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_leave_family,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetLeaveFamilyBinding>(
            layoutInflater,
            R.layout.bottom_sheet_leave_family,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)
        val yesBtn = view.findViewById<Button>(R.id.yes)!!
        val noBtn = view.findViewById<AppCompatTextView>(R.id.no)!!

        yesBtn.setOnClickListener {
            onLeaveFamilyClickListener.onLeaveClick()
            dismiss()
        }
        noBtn.setOnClickListener {
            dismiss()
        }


        return view
    }


    interface OnLeaveFamilyClickListener {
        fun onLeaveClick()
    }


}