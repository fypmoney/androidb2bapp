package com.fypmoney.view.kycagent.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
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

        if (!(arguments?.getString("message").isNullOrEmpty())) {
            kycRequestedSubmittedFragmentVM.message = arguments?.getString("message").toString()
        }

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = if (kycRequestedSubmittedFragmentVM.via == "UserKyc") "Complete Full KYC" else "Become a Fyp Agent!",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        binding.tvKycSubmittedSubHead.text = kycRequestedSubmittedFragmentVM.message

        binding.btnContinueClick.setOnClickListener {
            val intent = Intent(requireContext(), KycAgentActivity::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }

    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_kyc_requested_submitted

    override fun getViewModel(): KycRequestedSubmittedFragmentVM = kycRequestedSubmittedFragmentVM

    override fun onTryAgainClicked() {}


}