package com.fypmoney.view.kycagent.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentAgentAuthenticationBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.viewmodel.AgentAuthenticationFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class AgentAuthenticationFragment : BaseFragment<FragmentAgentAuthenticationBinding, AgentAuthenticationFragmentVM>() {

    private lateinit var binding: FragmentAgentAuthenticationBinding
    private val agentAuthenticationFragmentVM by viewModels<AgentAuthenticationFragmentVM> { defaultViewModelProviderFactory }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_agent_authentication

    override fun getViewModel(): AgentAuthenticationFragmentVM = agentAuthenticationFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        if (arguments?.containsKey("via") == true) {
            agentAuthenticationFragmentVM.via = arguments?.getString("via").toString()
        }

        if (arguments?.containsKey("mobileNumber") == true) {
            agentAuthenticationFragmentVM.mobileNumber =
                arguments?.getString("mobileNumber").toString()
        }

        agentAuthenticationFragmentVM.aadhaarNumber = arguments?.getString("aadhaarNumber")

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = if (agentAuthenticationFragmentVM.via == "UserKyc") "Complete Full KYC" else "Become a Fyp Agent!",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        if (agentAuthenticationFragmentVM.via == "UserKyc"){
            //userkyc
            binding.tvAgentAuthDesc.text = getString(R.string.place_customer_finger)
            binding.tvAgentAuthHead.text = "Biometric Authentication"
        }else{
            //selfkyc
            binding.tvAgentAuthDesc.text = getString(R.string.agent_to_place_finger_on_biometric_device_for_confirmation_please_ensure_that)
            binding.tvAgentAuthHead.text = "Agent Authentication"
        }

        binding.btnAddWithdrawSavings.setOnClickListener {
            if (binding.mcbConfirm.isChecked) {
                val bundle = Bundle()
                bundle.putString("aadhaarNumber", agentAuthenticationFragmentVM.aadhaarNumber)
                if (agentAuthenticationFragmentVM.mobileNumber != null)
                    bundle.putString("mobileNumber", agentAuthenticationFragmentVM.mobileNumber)
                bundle.putString("via", agentAuthenticationFragmentVM.via)
                findNavController().navigate(R.id.navigation_verify_biometric, bundle, navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                })
            }else{
                Utility.showToast("Please accept terms and conditions")
            }
        }

    }

    override fun onTryAgainClicked() {}

}