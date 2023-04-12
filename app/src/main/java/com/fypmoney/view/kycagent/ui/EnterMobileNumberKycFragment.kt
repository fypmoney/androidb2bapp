package com.fypmoney.view.kycagent.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentEnterMobileNumberKycBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.viewmodel.EnterMobileNumberKycFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class EnterMobileNumberKycFragment : BaseFragment<FragmentEnterMobileNumberKycBinding, EnterMobileNumberKycFragmentVM>() {

    private lateinit var binding: FragmentEnterMobileNumberKycBinding
    private val enterMobileNumberKycFragmentVM by viewModels<EnterMobileNumberKycFragmentVM> { defaultViewModelProviderFactory }

    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_enter_mobile_number_kyc

    override fun getViewModel(): EnterMobileNumberKycFragmentVM = enterMobileNumberKycFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Complete Full KYC",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        setUpObserver()

        binding.btnMobileNumberProceed.setOnClickListener {
            if (binding.etNumber.text?.trim().toString().isNotEmpty() && binding.etNumber.text?.trim()?.length == 10){

                enterMobileNumberKycFragmentVM.sendMobileNumberForOtp(binding.etNumber.text?.trim().toString())
            }else{
                Utility.showToast("Please enter valid mobile number")
            }
        }

    }

    private fun setUpObserver() {
        enterMobileNumberKycFragmentVM.state.observe(viewLifecycleOwner){
            when(it){
                is EnterMobileNumberKycFragmentVM.EnterMobileNumberKycState.Error -> {

                }
                EnterMobileNumberKycFragmentVM.EnterMobileNumberKycState.Loading -> {

                }
                is EnterMobileNumberKycFragmentVM.EnterMobileNumberKycState.Success -> {
                    if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        val bundle = Bundle()
                        bundle.putString("mobileNumber", binding.etNumber.text?.trim().toString())
                        bundle.putString("otpIdentifier", it.otpData.otpIdentifier)

                        findNavController().navigate(
                            R.id.navigation_enter_otp_kyc,
                            bundle,
                            navOptions {
                                anim {
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_righ
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                }
                            })
                    }
                }
            }
        }
    }

    override fun onTryAgainClicked() {}

}