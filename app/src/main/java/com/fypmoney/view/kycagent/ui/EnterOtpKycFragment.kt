package com.fypmoney.view.kycagent.ui

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentEnterOtpKycBinding
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.viewmodel.EnterOtpKycFragmentVM
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_enter_otp.*
import java.text.SimpleDateFormat
import java.util.*

class EnterOtpKycFragment : BaseFragment<FragmentEnterOtpKycBinding, EnterOtpKycFragmentVM>() {

    private lateinit var binding: FragmentEnterOtpKycBinding
    private val enterOtpKycFragmentVM by viewModels<EnterOtpKycFragmentVM> { defaultViewModelProviderFactory }
    lateinit var timer: CountDownTimer

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

        val data = arguments?.getString("otpIdentifier")

        if (arguments?.containsKey("mobileNumber") == true) {
            enterOtpKycFragmentVM.mobileNumber =
                arguments?.getString("mobileNumber").toString()
        }

        startTimer()

        binding.buttonContinue.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.otpView.text.toString()) -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.empty_otp_error))
                }
                else -> {
                    enterOtpKycFragmentVM.verifyOtp(
                        data!!, binding.otpView.text.toString()
                    )
                }
            }
        }

        binding.verificationTitle.text = String.format(
            getString(R.string.enter_the_otp_sent_to_to_verify_customer_s_request_for_full_kyc),
            enterOtpKycFragmentVM.mobileNumber
        )

        binding.tvSentOtpAgain.setOnClickListener {
        if (enterOtpKycFragmentVM.mobileNumber != null) {
            binding.tvSentOtpAgain.toInvisible()
            startTimer()
            enterOtpKycFragmentVM.resendMobileNumberForOtp(enterOtpKycFragmentVM.mobileNumber!!)
        }
        }

        setUpObserver()

    }

    private fun stopTimer() {
        timer.cancel()
    }

    private fun setUpObserver() {
        enterOtpKycFragmentVM.state.observe(viewLifecycleOwner){
            when(it){
                is EnterOtpKycFragmentVM.OtpVerifyState.Error -> {

                }
                EnterOtpKycFragmentVM.OtpVerifyState.Loading -> {

                }
                is EnterOtpKycFragmentVM.OtpVerifyState.Success -> {
                    if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        val bundle = Bundle()
                        bundle.putString("mobileNumber", enterOtpKycFragmentVM.mobileNumber)
                        bundle.putString("via", "UserKyc")
                        bundle.putString("fourDigits", it.otpVerifyData.aadhaarDigits)
                        findNavController().navigate(
                            R.id.navigation_enter_aadhaar_number_kyc,
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

        enterOtpKycFragmentVM.otpVerifyState.observe(viewLifecycleOwner){
            when(it){
                is EnterOtpKycFragmentVM.ResendOtpState.Error -> {

                }
                EnterOtpKycFragmentVM.ResendOtpState.Loading -> {

                }
                is EnterOtpKycFragmentVM.ResendOtpState.Success -> {
                    Utility.showToast("Otp Resend Successfully")
                }
            }
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvResendIn.toVisible()
                binding.tvResendIn.text = String.format(
                    getString(R.string.resend_timer_text) + SimpleDateFormat("00:ss").format(
                        Date(millisUntilFinished)
                    )
                )
            }

            override fun onFinish() {
                binding.tvResendIn.toInvisible()
                binding.tvSentOtpAgain.toVisible()
            }
        }.start()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_enter_otp_kyc

    override fun getViewModel(): EnterOtpKycFragmentVM = enterOtpKycFragmentVM

    override fun onTryAgainClicked() {}

}