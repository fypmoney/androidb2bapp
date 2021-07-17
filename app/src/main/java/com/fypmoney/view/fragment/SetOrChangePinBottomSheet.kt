package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetSetOrChangePinBinding
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/*
* This is used to set the pin
* */
class SetOrChangePinBottomSheet : BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_set_or_change_pin,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetSetOrChangePinBinding>(
            layoutInflater,
            R.layout.bottom_sheet_set_or_change_pin,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)
        bottomSheet.setCanceledOnTouchOutside(false)

        val gotBtn = view.findViewById<Button>(R.id.goToMessages)!!

        gotBtn.setOnClickListener {
            Utility.callMessagingApp(requireContext())
            dismiss()
        }


        return view
    }


}