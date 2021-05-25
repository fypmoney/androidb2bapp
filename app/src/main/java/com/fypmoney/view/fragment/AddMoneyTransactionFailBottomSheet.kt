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
import com.fypmoney.databinding.BottomSheetAddMoneyTransactionFailBinding
import com.fypmoney.databinding.BottomSheetLeaveMemberBinding
import com.fypmoney.util.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_leave_member.*


/*
* This is used to show transaction fail in case of add money
* */
class AddMoneyTransactionFailBottomSheet(
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
            R.layout.bottom_sheet_add_money_transaction_fail,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetAddMoneyTransactionFailBinding>(
            layoutInflater,
            R.layout.bottom_sheet_add_money_transaction_fail,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val amount = view.findViewById<TextView>(R.id.add_amount)!!

        amount.text =getString(R.string.add_money_trans_fail_add)+ getString(R.string.Rs) + amount1

        return view
    }

    interface OnBottomSheetClickListener {
        fun onBottomSheetButtonClick(type: String)

    }

}