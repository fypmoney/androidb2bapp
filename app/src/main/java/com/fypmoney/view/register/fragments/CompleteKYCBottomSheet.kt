package com.fypmoney.view.register.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomsheetCompleteKycBinding
import com.fypmoney.view.register.viewModel.CompleteKycBottomSheetVM

class CompleteKYCBottomSheet(var completeKycClicked:()->Unit) : BaseBottomSheetFragment<BottomsheetCompleteKycBinding>() {
    private val completeKYCBottomSheetVM by viewModels<CompleteKycBottomSheetVM> {
        defaultViewModelProviderFactory
    }
    override val baseFragmentVM: BaseViewModel
    get() = completeKYCBottomSheetVM
    override val customTag: String
    get() = CompleteKYCBottomSheet::class.java.simpleName
    override val layoutId: Int
    get() = R.layout.bottomsheet_complete_kyc

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
        setupObserver()
    }

    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = completeKYCBottomSheetVM
        }
    }
    private fun setupObserver() {
        completeKYCBottomSheetVM.event.observe(viewLifecycleOwner) {
            when (it) {
                CompleteKycBottomSheetVM.CompleteKycEvent.GotItEvent -> {
                    completeKycClicked()
                    dismiss()
                }
            }
        }
    }
}