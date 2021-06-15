package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetTransactionFailBinding
import com.fypmoney.util.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to show transaction fail in case of add money
* */
class TransactionFailBottomSheet(
    private val fromWhichScreen: String,
    private val amount1: String, private var onBottomSheetClickListener: OnBottomSheetClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_transaction_fail,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setCancelable(false)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetTransactionFailBinding>(
            layoutInflater,
            R.layout.bottom_sheet_transaction_fail,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val amount = view.findViewById<TextView>(R.id.add_amount)!!
        val changeMethod = view.findViewById<TextView>(R.id.changeMethod)!!
        val linearLayout = view.findViewById<LinearLayout>(R.id.linear_layout)!!
        val retry = view.findViewById<Button>(R.id.button_retry)!!
        val buttonRetryInCenter = view.findViewById<Button>(R.id.button_retry_InCenter)!!

        amount.text =
            getString(R.string.add_money_trans_fail_add) + getString(R.string.Rs) + amount1

        when (fromWhichScreen) {
            AppConstants.PAY -> {
                linearLayout.visibility = View.GONE
                buttonRetryInCenter.visibility = View.VISIBLE

            }
            else->{
            }
        }
        buttonRetryInCenter.setOnClickListener {
            dismiss()
        }

        changeMethod.setOnClickListener {
            dismiss()
        }
        retry.setOnClickListener {
            dismiss()
        }
        return view
    }

    interface OnBottomSheetClickListener {
        fun onBottomSheetButtonClick(type: String)

    }

}