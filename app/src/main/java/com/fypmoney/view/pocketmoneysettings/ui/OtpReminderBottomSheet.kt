package com.fypmoney.view.pocketmoneysettings.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomSheetPocketMoneyOtpBinding
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.model.SetPocketMoneyOtpReminder
import com.fypmoney.model.SetPocketMoneyReminder
import com.fypmoney.util.Utility
import com.fypmoney.view.pocketmoneysettings.viewmodel.OtpReminderVM
import java.text.SimpleDateFormat
import java.util.*

class OtpReminderBottomSheet : BaseBottomSheetFragment<BottomSheetPocketMoneyOtpBinding>(){

    private val otpReminderVM by viewModels<OtpReminderVM> { defaultViewModelProviderFactory }
    var otp = ObservableField<String>()
    private lateinit var timer: CountDownTimer
    private lateinit var notifyListener: OnActionCompleteListener

    fun setOnActionCompleteListener(notifyListener: OnActionCompleteListener) {
        this.notifyListener = notifyListener
    }

    override val baseFragmentVM: BaseViewModel
        get() = otpReminderVM
    override val customTag: String
        get() = OtpReminderBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottom_sheet_pocket_money_otp

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBinding()

        val touchOutsideView = dialog!!.window
            ?.decorView
            ?.findViewById<View>(R.id.touch_outside)
        touchOutsideView?.setOnClickListener(null)

        setUpObserver()

        startTimer()

        binding.otpView.setOtpCompletionListener { otp1 ->
            otp.set(otp1)
        }

        val data = arguments?.getString("otpIdentifier")

        binding.buttonContinue.setOnClickListener {
            when {
                TextUtils.isEmpty(otp.get()) -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.empty_otp_error))
                }
                else -> {
                    otpReminderVM.checkOtpPocketMoneyReminder(
                        SetPocketMoneyOtpReminder(
                            identifierType = "MOBILE",
                            otpIdentifier = data,
                            otp = otp.get()
                        )
                    )
                }
            }
        }

        binding.tvSentOtpAgain.setOnClickListener {
            otpReminderVM.callPocketMoneyResendOtp(
                SetPocketMoneyReminder(
                    identifierType = "MOBILE",
                    mobile = arguments?.getString("mobile"),
                    name = arguments?.getString("name"),
                    amount = arguments?.getInt("amount"),
                    frequency = arguments?.getString("frequency"),
                    relation = ""
                )
            )
        }

    }

    private fun setUpObserver() {
        otpReminderVM.stateReminderOtpVerify.observe(viewLifecycleOwner){
            handleOtpVerifyState(it)
        }

        otpReminderVM.stateReminderPocketMoney.observe(viewLifecycleOwner){
            handleOtpVerifyReminderState(it)
        }
    }

    private fun handleOtpVerifyReminderState(it: OtpReminderVM.PocketMoneyReminderState?) {
        when(it){
            is OtpReminderVM.PocketMoneyReminderState.Error -> {}
            OtpReminderVM.PocketMoneyReminderState.Loading -> {}
            is OtpReminderVM.PocketMoneyReminderState.Success -> {
                binding.tvSentOtpAgain.toInvisible()
                startTimer()
                Utility.showToast("Otp Resent")
            }
            null -> {}
        }
    }

    private fun handleOtpVerifyState(it: OtpReminderVM.PocketMoneyOtpVerifyState?) {
        when(it){
            is OtpReminderVM.PocketMoneyOtpVerifyState.Error -> {}
            OtpReminderVM.PocketMoneyOtpVerifyState.Loading -> {}
            is OtpReminderVM.PocketMoneyOtpVerifyState.Success -> {
                Utility.showToast("Reminder added successfully")
                notifyListener.onActionComplete("done")
                dismiss()
            }
            null -> {}
        }
    }

    interface OnActionCompleteListener {
        fun onActionComplete(data: String)
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

    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

}