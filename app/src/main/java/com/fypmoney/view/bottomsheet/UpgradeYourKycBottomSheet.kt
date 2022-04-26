package com.fypmoney.view.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomsheetUpgradeYourKycBinding

class UpgradeYourKycBottomSheet(var onUpgradeClick:()->Unit): BaseBottomSheetFragment<BottomsheetUpgradeYourKycBinding>() {
    private val upgradeYourKycBottomSheetVM by viewModels<UpgradeYourKycBottomSheetVM> {
        defaultViewModelProviderFactory
    }
    override val baseFragmentVM: BaseViewModel
        get() = upgradeYourKycBottomSheetVM
    override val customTag: String
        get() = UpgradeYourKycBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottomsheet_upgrade_your_kyc

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
        setupObserver()
    }

    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = upgradeYourKycBottomSheetVM
        }
    }
    private fun setupObserver() {
        upgradeYourKycBottomSheetVM.event.observe(viewLifecycleOwner,{
            when(it){
                UpgradeYourKycBottomSheetVM.UpgradeYourKycEvent.UpgradeYourKYC -> {
                    onUpgradeClick()
                    dismiss()

                }
            }
        })
    }
}