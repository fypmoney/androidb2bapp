package com.fypmoney.view.home.main.home.view

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
}