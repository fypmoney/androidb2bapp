package com.fypmoney.view.kycagent.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentKycRequestedSubmittedBinding
import com.fypmoney.view.kycagent.viewmodel.KycRequestedSubmittedFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class KycRequestedSubmittedFragment : BaseFragment<FragmentKycRequestedSubmittedBinding, KycRequestedSubmittedFragmentVM>() {

    private lateinit var binding: FragmentKycRequestedSubmittedBinding
    private val kycRequestedSubmittedFragmentVM by viewModels<KycRequestedSubmittedFragmentVM> { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        if (arguments?.containsKey("via") == true) {
            kycRequestedSubmittedFragmentVM.via = arguments?.getString("via").toString()
        }

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = if (kycRequestedSubmittedFragmentVM.via == "UserKyc") "Complete Full KYC" else "Become a Fyp Agent!",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )


    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_kyc_requested_submitted

    override fun getViewModel(): KycRequestedSubmittedFragmentVM = kycRequestedSubmittedFragmentVM

    override fun onTryAgainClicked() {}


}