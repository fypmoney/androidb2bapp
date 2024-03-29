package com.fypmoney.view.kycagent.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentEnterAadhaarNumberKycBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.viewmodel.EnterAadhaarNumberKycFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class EnterAadhaarNumberKycFragment : BaseFragment<FragmentEnterAadhaarNumberKycBinding, EnterAadhaarNumberKycFragmentVM>() {

    private lateinit var binding: FragmentEnterAadhaarNumberKycBinding
    private val enterAadhaarNumberKycFragmentVM by viewModels<EnterAadhaarNumberKycFragmentVM> { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        if (arguments?.containsKey("mobileNumber") == true) {
            enterAadhaarNumberKycFragmentVM.mobileNumber =
                arguments?.getString("mobileNumber").toString()
        }
        enterAadhaarNumberKycFragmentVM.via = arguments?.getString("via").toString()

        if (enterAadhaarNumberKycFragmentVM.via == "UserKyc"){
            //userkyc
            binding.tvAadhaarNumber.text = "Enter Aadhaar Number of Customer"
            trackr {
                it.name = TrackrEvent.new_aadhaar_view
            }
        }else{
            //selfkyc
            binding.tvAadhaarNumber.text = "Enter Your Aadhaar Number"
            trackr {
                it.name = TrackrEvent.signup_aadhaar_view
            }
        }

        if (arguments?.containsKey("fourDigits") == true) {
            enterAadhaarNumberKycFragmentVM.lastFourDigitCode = arguments?.getString("fourDigits")
        }else{
            enterAadhaarNumberKycFragmentVM.lastFourDigitCode = null
        }

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = if (enterAadhaarNumberKycFragmentVM.via == "UserKyc") "Complete Full KYC" else "Become a Fyp Agent!",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        binding.btnAddWithdrawSavings.setOnClickListener {
            binding.tvAaadhaarError.toGone()
            if (binding.etCity.text?.trim().toString().isNotEmpty() && binding.etCity.text?.trim()?.length == 12){
                if (enterAadhaarNumberKycFragmentVM.via == "UserKyc"){
                    //userkyc
                    trackr {
                        it.name = TrackrEvent.new_aadhaar_submit
                    }
                }else{
                    //selfkyc
                    trackr {
                        it.name = TrackrEvent.signup_aadhaar_submit
                    }
                }
                val lastFourDigits = binding.etCity.text?.trim().toString().substring(binding.etCity.text?.trim()?.length!! - 4)
                if (enterAadhaarNumberKycFragmentVM.lastFourDigitCode != null){
                    if (enterAadhaarNumberKycFragmentVM.lastFourDigitCode == lastFourDigits) {
                        openAgentAuthScreen()
                    }else{
                        binding.tvAaadhaarError.toVisible()
                    }
                }else{
                    openAgentAuthScreen()
                }

            }else{
                Utility.showToast("Please enter valid aadhaar number")
            }
        }
    }

    private fun openAgentAuthScreen(){
        val bundle = Bundle()
        bundle.putString("aadhaarNumber", binding.etCity.text?.trim().toString())
        if (enterAadhaarNumberKycFragmentVM.mobileNumber != null)
            bundle.putString(
                "mobileNumber",
                enterAadhaarNumberKycFragmentVM.mobileNumber
            )
        bundle.putString("via", enterAadhaarNumberKycFragmentVM.via)

        findNavController().navigate(
            R.id.navigation_agent_authentication,
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

    override fun getBindingVariable(): Int= BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_enter_aadhaar_number_kyc

    override fun getViewModel(): EnterAadhaarNumberKycFragmentVM = enterAadhaarNumberKycFragmentVM

    override fun onTryAgainClicked() {}

}