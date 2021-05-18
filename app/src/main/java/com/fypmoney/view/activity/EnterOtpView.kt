package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewEnterOtpBinding
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.EnterOtpViewModel
import kotlinx.android.synthetic.main.toolbar.*
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
            toolbar = toolbar
        )
        mViewModel.mobile.value = intent.getStringExtra(AppConstants.MOBILE_TYPE)
        mViewModel.mobileWithoutCountryCode.value =
            intent.getStringExtra(AppConstants.MOBILE_WITHOUT_COUNTRY_CODE)
        mViewBinding.viewModel = mViewModel
        setObservers()
        otpView.setOtpCompletionListener { otp -> // do Stuff
            mViewModel.otp.set(otp)
        }
        // start timer get started initially
        startTimer()
    }


    /**
     * set observers for view model variables
     */
    private fun setObservers() {
        mViewModel.onChangeClicked.observe(this) {
            if (it) {
                intentToActivity(LoginView::class.java)
                mViewModel.onChangeClicked.value = false
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
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@EnterOtpView, aClass))
        finish()
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