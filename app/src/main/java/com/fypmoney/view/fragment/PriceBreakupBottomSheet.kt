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
import com.fypmoney.databinding.BottomSheetPriceBreakupBinding
import com.fypmoney.model.GetAllProductsResponseDetails
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to show price Breakup
* */
class PriceBreakupBottomSheet(
    val amountValue: String?,
    val productResponse: GetAllProductsResponseDetails?
) :
    BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_price_breakup,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetPriceBreakupBinding>(
            layoutInflater,
            R.layout.bottom_sheet_price_breakup,
            null,
            false
        )

        bottomSheet.setContentView(bindingSheet.root)

        val amount = view.findViewById<TextView>(R.id.amount)!!
        val tax = view.findViewById<TextView>(R.id.tax)!!
        val itemTotal = view.findViewById<TextView>(R.id.item_total)!!
        val cardType = view.findViewById<TextView>(R.id.card_type)!!
        val gotBtn = view.findViewById<Button>(R.id.gotBtn)!!

        gotBtn.setOnClickListener { dismiss() }

        amount.text = getString(R.string.Rs) + amountValue
        tax.text = getString(R.string.Rs) + productResponse?.taxAmount
        cardType.text = productResponse?.name
        itemTotal.text =
            getString(R.string.Rs) + (amountValue?.toInt()!! - (productResponse?.taxAmount?.toInt()!! + productResponse.deleivceryCharge?.toInt()!! + productResponse.taxAmount.toInt()))


        return view
    }


}