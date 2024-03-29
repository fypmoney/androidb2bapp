package com.fypmoney.view.activity

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewEnterOtpBinding
import com.fypmoney.receivers.AutoReadOtpUtils
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.EnterOtpViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_aadhaar_account_activation.*
import kotlinx.android.synthetic.main.view_enter_otp.*
import kotlinx.android.synthetic.main.view_enter_otp.btnSendOtp
import kotlinx.android.synthetic.main.view_login.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * This is used to handle the OTP
 */
class EnterOtpView : BaseActivity<ViewEnterOtpBinding, EnterOtpViewModel>() {
    private lateinit var mViewModel: EnterOtpViewModel
    private lateinit var mViewBinding: ViewEnterOtpBinding
    lateinit var timer: CountDownTimer
    //val smsBroadcastReceiver = SmsBroadcastReceiver()
    lateinit var autoReadOtpUtils:AutoReadOtpUtils

    var isRegistered:Boolean = false
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
        autoReadOtpUtils = AutoReadOtpUtils(this)
        autoReadOtpUtils.initialise()

        autoReadOtpUtils.registerOtpReceiver {
            otpView.setText(it)
            mViewModel.onVerifyClicked()
        }
        callAutoReadOtpSetup()
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

        //keyboard.setInputConnection(ic)
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

        mViewBinding.otpView.doOnTextChanged { text, start, before, count ->
            if((text?.length)!! <4){
                btnSendOtp.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                    this,
                    R.color.buttonUnselectedColor
                ))
                btnSendOtp.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_little_dark
                    )
                )
                btnSendOtp.isEnabled = false
            }else{
                btnSendOtp.isEnabled = true

                btnSendOtp.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                    this,
                    R.color.black
                ))
                btnSendOtp.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
            }
        }


    }

    private fun callAutoReadOtpSetup() {
       /* val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener { aVoid ->
            isRegistered = true
            smsBroadcastReceiver.setOnOtpListeners(object : OtpReceivedInterface {
                override fun onOtpReceived(otp: String?) {
                    otpView.setText(otp)
                    mViewModel.onVerifyClicked()
                    this@EnterOtpView.unregisterReceiver(smsBroadcastReceiver)
                    isRegistered = false

                }

                override fun onOtpTimeout() {
                    Log.v("Otp", "Otp time out")
                    this@EnterOtpView.unregisterReceiver(smsBroadcastReceiver)
                    isRegistered = false

                }

            })
            registerReceiver(
                smsBroadcastReceiver,
                IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
            )
        }
        task.addOnFailureListener { e ->
            Log.e("auto sms read", e.toString())
            isRegistered = false

        }*/
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

                mViewModel.onVerificationFail.value = false
                finish()
            }
        }
        mViewModel.upgradeKycFailed.observe(this) {
            if (it) {
                mViewModel.upgradeKycFailed.value = false
                finish()
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
                intentToActivity(LoginSuccessView::class.java, isFinish = true)
                mViewModel.onLoginSuccess.value = false
            }
        }

        mViewModel.onVerificationSuccess.observe(this) {
            if (it) {
                when (mViewModel.fromWhichScreen.get()) {
//
                    AppConstants.AADHAAR_VERIFICATION -> {
                        trackr {
                            it.name = TrackrEvent.upgrade_kyc_successfully
                        }
                        when(intent.getStringExtra(AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN)){
                            AddMoneyView::class.java.simpleName->{
                                startActivity(Intent(this@EnterOtpView,AddMoneyView::class.java))
                            }
                            PayRequestProfileView::class.java.simpleName->{
                                startActivity(Intent(this@EnterOtpView,PayRequestProfileView::class.java))

                            }
                            UserProfileView::class.java.simpleName->{
                                startActivity(Intent(this@EnterOtpView,UserProfileView::class.java))

                            }
                        }
                    }
                    AppConstants.KYC_MOBILE_VERIFICATION -> {
                        intentToActivity(AadhaarVerificationView::class.java)
                        finish()
                    }
                }
                mViewModel.onVerificationSuccess.value = false


            }

        }
        mViewModel.onVerificationSuccessAadhaar.observe(this) {
            if (it != null) {

                trackr {
                    it.services = arrayListOf(
                        TrackrServices.FIREBASE,
                        TrackrServices.MOENGAGE,
                        TrackrServices.FB
                    )
                    it.name = TrackrEvent.kyc_verification
                }
                val intent = Intent(this, ActivationSuccessWithAadhaarView::class.java)
                intent.putExtra(AppConstants.POSTKYCKEY, it.postKycScreenCode)
                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    R.anim.slideinleft,
                    R.anim.slideinright
                ).toBundle()
                startActivity(intent, bndlAnimation)
                finishAffinity()




                mViewModel.onVerificationSuccessAadhaar.value = null


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


    override fun onDestroy() {
        super.onDestroy()
        /*if(isRegistered){
            this@EnterOtpView.unregisterReceiver(smsBroadcastReceiver)

        }*/
        autoReadOtpUtils.unregisterOtpReceiver()


    }
}