package com.fypmoney.view.fragment


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetPriceBreakupBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.ordercard.model.UserOfferCard
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to show price Breakup
* */
class PriceBreakupBottomSheet(
    val userOfferCard: UserOfferCard
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetPriceBreakupBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_price_breakup,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.amount.text = "${getString(R.string.Rs)} ${Utility.convertToRs(userOfferCard.basePrice.toString())}"
        binding.discountAmountTv.text = "${getString(R.string.Rs)} ${Utility.convertToRs(userOfferCard.discount.toString())}"
        binding.netPaybleAmountTv.text = "${getString(R.string.Rs)} ${Utility.convertToRs(userOfferCard.mrp.toString())}"
        binding.taxAmountTv.text = String.format(getString(R.string.inc_tax),Utility.convertToRs(userOfferCard.totalTax.toString()))
        binding.gotItBtn.setOnClickListener {
            dismiss()
        }
    }


}