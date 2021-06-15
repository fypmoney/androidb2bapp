package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetAddUpiBinding
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.payu.india.Payu.PayuConstants


/*
* This is used to show new upi added
* */
class AddUpiBottomSheet(
    private val amount: String?,
    private var onBottomSheetClickListener: OnAddUpiClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_add_upi,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetAddUpiBinding>(
            layoutInflater,
            R.layout.bottom_sheet_add_upi,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val upiId = view.findViewById<EditText>(R.id.upiId)!!
        val saveCardCheckbox = view.findViewById<CheckBox>(R.id.saveCardCheckbox)!!
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)!!

        btnAdd.text = getString(R.string.add_btn_text) + " " + getString(R.string.Rs) + amount



        btnAdd.setOnClickListener {

            when {
                upiId.length() == 0 -> {
                    Utility.showToast(getString(R.string.add_upi_empty_error))

                }
                upiId.length() > PayuConstants.MAX_VPA_SIZE -> {
                    Utility.showToast(getString(R.string.invalid_upi_error))

                }
                !upiId.text.toString().trim().contains("@") -> {
                    Utility.showToast(getString(R.string.invalid_upi_error))

                }
                else -> {
                    onBottomSheetClickListener.onAddUpiClickListener(
                        upiId.text.toString(), saveCardCheckbox.isChecked
                    )
                    dismiss()

                }
            }


        }

        return view
    }

    interface OnAddUpiClickListener {
        fun onAddUpiClickListener(upiId: String, isUpiSaved: Boolean)
    }

}