package com.fypmoney.view.ordercard.trackorder

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetShippingDetailsBinding
import com.fypmoney.model.PackageStatusList
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ShippingDetailsBottomSheet(val packageStatusList: PackageStatusList,
                                 val onTrackOrderClick:(packageStatusList: PackageStatusList)->Unit
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetShippingDetailsBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_shipping_details,
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
        binding.delieveryPartnerNameValueTv.text = packageStatusList.partnerName
        binding.awbNumberValueTv.text =  packageStatusList.awbNo
        binding.expectedDeliveryValueTv.text =  packageStatusList.expectedDeliveryDate
        binding.trackOrderButton.setOnClickListener {
            dismiss()
            onTrackOrderClick(packageStatusList)
        }
    }


}