package com.fypmoney.view.fragment


import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetEarnedCoinsBinding
import com.fypmoney.databinding.BottomSheetRedeemCoinsBinding
import com.fypmoney.databinding.DialogCashWonProductBinding
import com.fypmoney.model.RedeemDetailsResponse
import com.fypmoney.view.interfaces.ListItemClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_redeem_coins.*
import kotlinx.android.synthetic.main.bottom_sheet_redeem_coins.view.*


class MoneyEarnedMyntsBottomSheet(

) :
    BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    var otp = ObservableField<String>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.dialog_cash_won_product,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<DialogCashWonProductBinding>(
            layoutInflater,
            R.layout.dialog_cash_won_product,
            null,
            false
        )


        bottomSheet.setContentView(bindingSheet.root)



        return view
    }


}