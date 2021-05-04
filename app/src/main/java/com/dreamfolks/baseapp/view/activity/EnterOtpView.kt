package com.dreamfolks.baseapp.view.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.dreamfolks.baseapp.BR
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.base.BaseActivity
import com.dreamfolks.baseapp.databinding.ViewEnterOtpBinding
import com.dreamfolks.baseapp.model.CustomerInfoResponse
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.viewmodel.EnterOtpViewModel
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsClient
import com.google.android.gms.auth.api.credentials.HintRequest
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
            toolbar = toolbar,
            textView = toolbar_title,
            toolbarTitle = getString(R.string.enter_otp)
        )
        mViewModel.mobile.value = intent.getStringExtra(AppConstants.MOBILE_TYPE)
        mViewModel.mobileWithoutCountryCode.value =
            intent.getStringExtra(AppConstants.MOBILE_WITHOUT_COUNTRY_CODE)
        mViewBinding.viewModel = mViewModel
        setObservers()
        otpView.setOtpCompletionListener { otp -> // do Stuff
            mViewModel.otp.set(otp)
        }
        // make resend otp visible after time specified
        Handler(Looper.getMainLooper()).postDelayed({
            mViewModel.resendOtpVisibility.set(true)
        }, AppConstants.RESEND_OTP_DURATION)


    }

    /*
    * This method is used for asking phone number
    * */
    private fun requestPhoneNumberHint(currentActivity: Activity?) {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val credentialsClient: CredentialsClient = Credentials.getClient(currentActivity!!)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        try {
            startIntentSenderForResult(
                intent.intentSender,
                1008, null, 0, 0, 0
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
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
        mViewModel.cancelTimer.observe(this) {
            if (it) {
                stopTimer()
                mViewModel.cancelTimer.value = false
            }
        }
        mViewModel.getCustomerInfoSuccess.observe(this) {
            when (it.isProfileCompleted) {
                AppConstants.YES -> {
                    intentToActivity(HomeView::class.java)
                }
                AppConstants.NO -> {
                    intentToSelectInterestActivity(it, SelectInterestView::class.java)
                }

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
    }

    /**
     * Method to navigate to the select interest activity
     */
    private fun intentToSelectInterestActivity(
        customerInfoResponseData: CustomerInfoResponse,
        aClass: Class<*>
    ) {
        val intent = Intent(this@EnterOtpView, aClass)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, customerInfoResponseData)
        startActivity(intent)
    }

    private fun startTimer() {
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mViewModel.isResendEnabled.set(false)
                tvSentOtpAgain.text = "Resend in " + SimpleDateFormat("00:ss").format(
                    Date(millisUntilFinished)
                )
            }

            override fun onFinish() {
                mViewModel.isResendEnabled.set(true)
                tvSentOtpAgain.text = getString(R.string.resend_otp)
            }
        }.start()
    }

    fun stopTimer() {
        timer.cancel()
    }


}