package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetPriceBreakupBinding
import com.fypmoney.model.GetAllProductsResponseDetails
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to show price Breakup
* */
class PriceBreakupBottomSheet(
    val amountValue: String?,
    val productResponse: GetAllProductsResponseDetails?, var isDiscountVisible: Int? = 0
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
        val cardType = view.findViewById<TextView>(R.id.card_type)!!
        val tax = view.findViewById<TextView>(R.id.tax)!!
        val discount = view.findViewById<TextView>(R.id.discount)!!
        val itemTotal = view.findViewById<TextView>(R.id.item_total)!!
        val gotBtn = view.findViewById<Button>(R.id.gotBtn)!!
        val discountRow = view.findViewById<TableRow>(R.id.discountRow)!!

        when (isDiscountVisible) {
            1 -> {
                discountRow.visibility = View.VISIBLE
            }
            else -> {
                discountRow.visibility = View.GONE

            }

        }

        gotBtn.setOnClickListener { dismiss() }

        amount.text = getString(R.string.Rs) + Utility.convertToRs(productResponse?.basePrice)
        tax.text = getString(R.string.Rs) + Utility.convertToRs(productResponse?.totalTax)
        cardType.text = productResponse?.name
        discount.text = productResponse?.discount
        itemTotal.text = Utility.convertToRs(productResponse?.mrp)


        return view
    }


}