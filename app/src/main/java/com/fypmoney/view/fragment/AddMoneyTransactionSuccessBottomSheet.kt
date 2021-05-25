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
import com.fypmoney.databinding.BottomSheetAddMoneyTransactionSuccessBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/*
* This is used to show transaction success in case of add money
* */
class AddMoneyTransactionSuccessBottomSheet(
     private val amount1: String, private val updatedAmount1: String,
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_add_money_transaction_success,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetAddMoneyTransactionSuccessBinding>(
            layoutInflater,
            R.layout.bottom_sheet_add_money_transaction_success,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val amount = view.findViewById<TextView>(R.id.amount)!!
        val updatedAmount = view.findViewById<TextView>(R.id.updated_balance)!!

        amount.text = getString(R.string.Rs) + amount1
        updatedAmount.text = getString(R.string.updated_balance) + getString(R.string.Rs)+ updatedAmount1

        return view
    }


}