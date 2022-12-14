package com.fypmoney.view.activity

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewLoginBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.credentials.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_add_money.*
import kotlinx.android.synthetic.main.view_login.*
import kotlinx.android.synthetic.main.view_login.btnSendOtp


/**
 * Login screen - view
 */
class LoginView : BaseActivity<ViewLoginBinding, LoginViewModel>() {

    private lateinit var mViewModel: LoginViewModel
    private lateinit var mViewBinding: ViewLoginBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_login
    }

    override fun getViewModel(): LoginViewModel {
        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewModel.callAuthLoginApi()
        setToolbarAndTitle(
            context = this@LoginView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        requestPhoneNumberHint()
        mViewBinding.activity = this
        mViewBinding.viewModel = mViewModel
        setObservers()
    }

    /*
    * This method is used for asking phone number
    * */
    private fun requestPhoneNumberHint() {
        val options = CredentialsOptions.Builder()
            .build()
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val credentialsClient: CredentialsClient = Credentials.getClient(applicationContext,options)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        try {
            startIntentSenderForResult(
                intent.intentSender,
                1008, null, 0, 0, 0,null
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }
    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(this@LoginView, aClass)
        startActivity(intent)
    }

    /*
      * Method to set the length of mobile field
      * */


    /**
     * set observers for view model variables
     */
    private fun setObservers() {
        mViewModel.onMobileClicked.observe(this) {
            if (it) {
                mViewModel.onMobileClicked.value = false
            }
        }
        mViewModel.onContinueClicked.observe(this) {
            if (it) {
                mViewModel.callSendOtpApi()
                mViewModel.onContinueClicked.value = false
            }
        }
        mViewModel.onOtpSentSuccess.observe(this) {
            if (it) {
                goToEnterOtpScreen()
                mViewModel.onOtpSentSuccess.value = false
            }
        }

        mViewBinding.etStart.doOnTextChanged { text, start, before, count ->
            if(text?.length!=10){
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


    /*invoke method for backpressed
    * */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1008 -> if (resultCode == RESULT_OK) {
                val cred: Credential = data?.getParcelableExtra(Credential.EXTRA_KEY)!!
                //                    cred.getId====: ====+919*******
                mViewModel.mobile.value = Utility.deleteCountryCode(cred.id)
                Log.e("cred.getId", cred.id)
                val userMob = cred.id
            } else {
                // Sim Card not found!
                mViewModel.mobile.value = ""
                mViewModel.isMobileFocusable.set(true)
                mViewModel.isMobileEnabled.set(false)
                mViewModel.isMobileEditableVisible.set(true)
                return
            }
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        requestPhoneNumberHint()
                    }

                }
            }
        }
    }


    /**
     * Method to navigate to the Feeds screen after login
     */
    private fun goToEnterOtpScreen() {
        val intent = Intent(this, EnterOtpView::class.java)
        intent.putExtra(
            AppConstants.MOBILE_TYPE,
            "91"+ mViewModel.mobile.value
        )
        intent.putExtra(
            AppConstants.FROM_WHICH_SCREEN, AppConstants.LOGIN_SCREEN
        )

        intent.putExtra(
            AppConstants.MOBILE_WITHOUT_COUNTRY_CODE,
            mViewModel.mobile.value
        )

        intent.putExtra(
            AppConstants.KIT_FOUR_DIGIT, ""

        )

        startActivity(intent)
    }




}