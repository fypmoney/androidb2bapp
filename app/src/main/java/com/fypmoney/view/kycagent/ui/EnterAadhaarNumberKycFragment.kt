package com.fypmoney.view.kycagent.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentEnterAadhaarNumberKycBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.viewmodel.EnterAadhaarNumberKycFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class EnterAadhaarNumberKycFragment : BaseFragment<FragmentEnterAadhaarNumberKycBinding, EnterAadhaarNumberKycFragmentVM>() {

    private lateinit var binding: FragmentEnterAadhaarNumberKycBinding
    private val enterAadhaarNumberKycFragmentVM by viewModels<EnterAadhaarNumberKycFragmentVM> { defaultViewModelProviderFactory }

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

        enterAadhaarNumberKycFragmentVM.mobileNumber = arguments?.getString("mobile").toString()

        binding.btnAddWithdrawSavings.setOnClickListener {
            if (binding.etCity.text?.trim().toString().isNotEmpty() && binding.etCity.text?.trim()?.length == 12){
                val bundle = Bundle()
                bundle.putString("aadhaar_number", binding.etCity.text?.trim().toString())
                bundle.putString("mobile_number", enterAadhaarNumberKycFragmentVM.mobileNumber)
                bundle.putString("via", "UserKyc")

                findNavController().navigate(R.id.navigation_agent_authentication, bundle, navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                })

            }else{
                Utility.showToast("Please enter valid aadhaar number")
            }
        }
    }

    override fun getBindingVariable(): Int= BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_enter_aadhaar_number_kyc

    override fun getViewModel(): EnterAadhaarNumberKycFragmentVM = enterAadhaarNumberKycFragmentVM

    override fun onTryAgainClicked() {}

}