package com.fypmoney.view.pocketmoneysettings.ui

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.databinding.BottomSheetPocketMoneyOtpBinding
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.model.SetPocketMoneyOtpReminder
import com.fypmoney.model.SetPocketMoneyReminder
import com.fypmoney.util.Utility
import com.fypmoney.view.pocketmoneysettings.model.Data
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyOtpVerifyResponse
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyReminderResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class OtpReminderBottomSheet : BottomSheetDialogFragment(), WebApiCaller.OnWebApiResponse {

    private lateinit var binding: BottomSheetPocketMoneyOtpBinding
    var otp = ObservableField<String>()
    private lateinit var timer: CountDownTimer
    private lateinit var notifyListener: OnActionCompleteListener

    fun setOnActionCompleteListener(notifyListener: OnActionCompleteListener) {
        this.notifyListener = notifyListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_pocket_money_otp,
            container,
            false
        )

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
                    checkOtpPocketMoneyReminder(
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
            WebApiCaller.getInstance().request(
                ApiRequest(
                    ApiConstant.API_ADD_POCKET_MONEY_REMINDER,
                    NetworkUtil.endURL(ApiConstant.API_ADD_POCKET_MONEY_REMINDER),
                    ApiUrl.POST,
                    SetPocketMoneyReminder(
                        identifierType = "MOBILE",
                        mobile = arguments?.getString("mobile"),
                        name = arguments?.getString("name"),
                        amount = arguments?.getInt("amount"),
                        frequency = arguments?.getString("frequency"),
                        relation = ""
                    ),
                    this, isProgressBar = true
                )
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val touchOutsideView = dialog!!.window
            ?.decorView
            ?.findViewById<View>(R.id.touch_outside)
        touchOutsideView?.setOnClickListener(null)
    }

    interface OnActionCompleteListener {
        fun onActionComplete(data: String)
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun progress(isStart: Boolean, message: String) {
    }

    private fun startTimer() {
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvResendIn.toVisible()
                binding.tvResendIn.text =
                    getString(R.string.resend_timer_text) + SimpleDateFormat("00:ss").format(
                        Date(millisUntilFinished)
                    )
            }

            override fun onFinish() {
                binding.tvResendIn.toInvisible()
                binding.tvSentOtpAgain.toVisible()
            }
        }.start()
    }

    private fun checkOtpPocketMoneyReminder(setPocketMoneyOtpReminder: SetPocketMoneyOtpReminder) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_VERIFY_OTP_POCKET_MONEY_REMINDER,
                NetworkUtil.endURL(ApiConstant.API_VERIFY_OTP_POCKET_MONEY_REMINDER),
                ApiUrl.POST,
                setPocketMoneyOtpReminder,
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        when (purpose) {
            ApiConstant.API_VERIFY_OTP_POCKET_MONEY_REMINDER -> {
                if (responseData is PocketMoneyOtpVerifyResponse) {
                    Utility.showToast("Reminder added successfully")
                    notifyListener.onActionComplete("done")
                    dismiss()
                }
            }
            ApiConstant.API_ADD_POCKET_MONEY_REMINDER -> {
                if (responseData is PocketMoneyReminderResponse) {
                    binding.tvSentOtpAgain.toInvisible()
                    startTimer()
                    Utility.showToast("Otp Resent")
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        when (purpose) {
            ApiConstant.API_VERIFY_OTP_POCKET_MONEY_REMINDER -> {
                Utility.showToast(errorResponseInfo.msg)
            }
            ApiConstant.API_ADD_POCKET_MONEY_REMINDER -> {
                Utility.showToast(errorResponseInfo.msg)
            }
        }
    }

    override fun offLine() {

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}