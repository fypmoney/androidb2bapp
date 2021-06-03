package com.fypmoney.view.fragment


import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetInviteBinding
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to update family name
* */
class InviteBottomSheet(var clipboardManager: ClipboardManager) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_invite,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetInviteBinding>(
            layoutInflater,
            R.layout.bottom_sheet_invite,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val referralCode = view.findViewById<TextView>(R.id.title1)!!
        val referralMsg = view.findViewById<TextView>(R.id.title3)!!

        referralCode.text = Utility.getCustomerDataFromPreference()?.referralCode
        referralMsg.text = Utility.getCustomerDataFromPreference()?.referralMsg


        referralCode.setOnClickListener {
                 val clip = ClipData.newPlainText("label", Utility.getCustomerDataFromPreference()?.referralCode)
            clipboardManager.setPrimaryClip(clip)        }
        return view
    }

    interface OnUpdateFamilyClickListener {
        fun onUpdateFamilyButtonClick(familyName:String)

    }

}