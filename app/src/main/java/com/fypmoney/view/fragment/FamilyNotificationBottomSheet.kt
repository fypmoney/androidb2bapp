package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetFamilyNotificationBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to show transaction fail in case of add money
* */
class FamilyNotificationBottomSheet(
    private val actionAllowed: String?,
    private val descriptionValue: String?,
    private var onBottomSheetClickListener: OnBottomSheetClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_family_notification,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetFamilyNotificationBinding>(
            layoutInflater,
            R.layout.bottom_sheet_family_notification,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val description = view.findViewById<TextView>(R.id.description)!!
        val btnReject = view.findViewById<TextView>(R.id.button_reject)!!
        val btnAccept = view.findViewById<TextView>(R.id.button_accept)!!
        val btnCancel = view.findViewById<TextView>(R.id.button_cancel)!!

        val actionAllowedList = actionAllowed?.split(",")
        if (actionAllowedList?.size!! > 1) {
            btnCancel.visibility = View.GONE

        } else {
            btnAccept.visibility = View.GONE
            btnReject.visibility = View.GONE

        }


        description.text = descriptionValue

        btnReject.setOnClickListener {
            onBottomSheetClickListener.onBottomSheetButtonClick(actionAllowedList[1])
            dismiss()
        }
        btnAccept.setOnClickListener {
            onBottomSheetClickListener.onBottomSheetButtonClick(actionAllowedList[0])
            dismiss()
        }

        btnCancel.setOnClickListener {
            onBottomSheetClickListener.onBottomSheetButtonClick(actionAllowed)
            dismiss()
        }
        return view
    }

    interface OnBottomSheetClickListener {
        fun onBottomSheetButtonClick(actionAllowed: String?)

    }

}