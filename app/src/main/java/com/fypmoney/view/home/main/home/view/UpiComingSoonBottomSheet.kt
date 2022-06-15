package com.fypmoney.view.home.main.home.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomsheetUpiComingSoonBinding
import com.fypmoney.view.home.main.home.viewmodel.UpiComingSoonVM

class UpiComingSoonBottomSheet: BaseBottomSheetFragment<BottomsheetUpiComingSoonBinding>() {
    private val upiScanComingSoonViewModel by viewModels<UpiComingSoonVM> {
        defaultViewModelProviderFactory
    }
    override val baseFragmentVM: BaseViewModel
        get() = upiScanComingSoonViewModel
    override val customTag: String
        get() = UpiComingSoonBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottomsheet_upi_coming_soon

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
        setupObserver()
    }

    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = upiScanComingSoonViewModel
        }
    }
    private fun setupObserver() {
        upiScanComingSoonViewModel.event.observe(viewLifecycleOwner) {
            when (it) {
                UpiComingSoonVM.UpiComingSoonEvent.GotItEvent -> {
                    dismiss()
                }
            }
        }
    }
}