package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewEnterOtpBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.EnterOtpViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_aadhaar_account_activation.*
import kotlinx.android.synthetic.main.view_enter_otp.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * This is used to handle the OTP
 */
class EnterOtpView : BaseActivity<ViewEnterOtpBinding, EnterOtpViewModel>() {
    private lateinit var mViewModel: EnterOtpViewModel
    private lateinit var mViewBinding: ViewEnterOtpBinding
    lateinit var timer: CountDownTimer
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_enter_otp
    }

    override fun getViewModel(): EnterOtpViewModel {
        mViewModel = ViewModelProvider(this).get(EnterOtpViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@EnterOtpView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        mViewModel.setInitialData(
            intent.getStringExtra(AppConstants.MOBILE_TYPE),
            intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN),
            intent.getStringExtra(AppConstants.KYC_ACTIVATION_TOKEN),
            intent.getStringExtra(AppConstants.KIT_FOUR_DIGIT),
        )
        mViewModel.mobileWithoutCountryCode.value =
            intent.getStringExtra(AppConstants.MOBILE_WITHOUT_COUNTRY_CODE)
        mViewBinding.viewModel = mViewModel
        setObservers()
        otpView.setOtpCompletionListener { otp -> // do Stuff
            mViewModel.otp.set(otp)
        }

        val ic: InputConnection? = otpView.onCreateInputConnection(EditorInfo())
        keyboard.setInputConnection(ic)
        // start timer get started initially
        startTimer()

        when (mViewModel.fromWhichScreen.get()) {
            AppConstants.AADHAAR_VERIFICATION -> {
                otpView.itemSpacing = 15
                otpView.itemCount = 6

                val ss = SpannableString(mViewModel.mobile.value)
                val clickableSpan: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        intentToActivity(
                            AadhaarVerificationView::class.java,
                            isFinish = true
                        )
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                        ds.color =
                            ContextCompat.getColor(applicationContext, R.color.text_color_dark)
                    }
                }
                try {
                    ss.setSpan(clickableSpan, 64, 83, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tv_mobile.text = ss
                    tv_mobile.movementMethod = LinkMovementMethod.getInstance()

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }

            AppConstants.KYC_MOBILE_VERIFICATION -> {
                otpView.itemSpacing = 15
                otpView.itemCount = 6


            }
        }


    }


    /**
     * set observers for view model variables
     */
    private fun setObservers() {
        mViewModel.onChangeClicked.observe(this) {
            if (it) {
                when (mViewModel.fromWhichScreen.get()) {
                    AppConstants.LOGIN_SCREEN -> {
                        intentToActivity(LoginView::class.java)
                    }
                    else -> {
                        intentToActivity(AadhaarVerificationView::class.java)
                        finish()

                    }
                }
                mViewModel.onChangeClicked.value = false
            }
        }

        mViewModel.onVerificationFail.observe(this) {
            if (it) {
                intentToActivity(AadhaarAccountActivationView::class.java)
                mViewModel.onVerificationFail.value = false
                finishAffinity()
            }
        }
        mViewModel.resendOtpSuccess.observe(this) {
            if (it) {
                startTimer()
                mViewModel.resendOtpSuccess.value = false
            }
        }

        mViewModel.onLoginSuccess.observe(this) {
            if (it) {
                intentToActivity(LoginSuccessView::class.java)
                mViewModel.onLoginSuccess.value = false
            }
        }

        mViewModel.onVerificationSuccess.observe(this) {
            if (it) {
                when (mViewModel.fromWhichScreen.get()) {
                    AppConstants.AADHAAR_VERIFICATION -> {
                        intentToActivity(
                            ActivationSuccessWithAadhaarView::class.java,
                            isFinish = true
                        )


                    }
                    AppConstants.KYC_MOBILE_VERIFICATION -> {
                        intentToActivity(AadhaarVerificationView::class.java)
                        finish()

                    }
                }
                mViewModel.onVerificationSuccess.value = false


            }

        }
        mViewModel.cancelTimer.observe(this) {
            if (it) {
                stopTimer()
                mViewModel.cancelTimer.value = false
            }
        }


    }


    /*invoke method for backpressed
    * */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(
        aClass: Class<*>,
        isFinish: Boolean? = false
    ) {
        startActivity(Intent(this@EnterOtpView, aClass))
        if (isFinish!!) {
            finishAffinity()
        }
    }

    /*
    * This method is used to start the timer
    * */
    private fun startTimer() {
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mViewModel.isResendEnabled.set(false)
                tvResendIn.text =
                    getString(R.string.resend_timer_text) + SimpleDateFormat("00:ss").format(
                        Date(millisUntilFinished)
                    ) + "sec"
            }

            override fun onFinish() {
                mViewModel.resendOtpTimerVisibility.set(false)
                mViewModel.isResendEnabled.set(true)
            }
        }.start()
    }

    private fun stopTimer() {
        timer.cancel()
    }


}