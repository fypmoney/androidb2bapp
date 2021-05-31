package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetFamilyNotificationBinding
import com.fypmoney.databinding.BottomSheetUpdateFamilyNameBinding
import com.fypmoney.util.SharedPrefUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to update family name
* */
class UpdateFamilyNameBottomSheet(
    private var onBottomSheetClickListener: OnUpdateFamilyClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_update_family_name,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetUpdateFamilyNameBinding>(
            layoutInflater,
            R.layout.bottom_sheet_update_family_name,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val familyName = view.findViewById<EditText>(R.id.et_first_name)!!
        val btnUpdate = view.findViewById<Button>(R.id.button_update)!!
        val btnCancel = view.findViewById<TextView>(R.id.button_cancel)!!

        familyName.setText(SharedPrefUtils.getString(requireContext(),SharedPrefUtils.SF_KEY_USERNAME))

        btnUpdate.setOnClickListener {
            onBottomSheetClickListener.onUpdateFamilyButtonClick(familyName.text.toString())
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
        return view
    }

    interface OnUpdateFamilyClickListener {
        fun onUpdateFamilyButtonClick(familyName:String)

    }

}