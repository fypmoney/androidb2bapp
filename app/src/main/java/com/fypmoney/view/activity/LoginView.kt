package com.fypmoney.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewLoginBinding
import com.fypmoney.receivers.SmsBroadcastReceiver
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.CountryCodeArrayAdapter
import com.fypmoney.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsClient
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_login.*


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
            isBackArrowVisible = false
        )
        requestPhoneNumberHint(this)

        // spannable string

        val ss = SpannableString(getString(R.string.back_to_login))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                //  intentToActivity(LoginView::class.java)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(applicationContext, R.color.text_color_dark)
            }
        }
        ss.setSpan(clickableSpan, 7, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        back_to_login.text = ss
        back_to_login.movementMethod = LinkMovementMethod.getInstance()


        // auto sms read
        //        Initialize the SmsRetriever client
        val client = SmsRetriever.getClient(this)
//        Start the SMS Retriever task
        val task = client.startSmsRetriever()
        task.addOnSuccessListener { aVoid ->
//            if successfully started, then start the receiver.
            smsBroadcastReceiver = SmsBroadcastReceiver()
            registerReceiver(
                smsBroadcastReceiver,
                IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
            )

        }
        task.addOnFailureListener { e ->
            //            if failure print the exception.
            Log.e("auto sms read", e.toString())
        }

        mViewBinding.activity = this
        mViewBinding.viewModel = mViewModel
        setObservers()
        setCountryCodeAdapter(applicationContext, mViewBinding.spCountryCode)

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

    private fun setCountryCodeAdapter(context: Context, spCountryCode: AppCompatSpinner) {
        val adapterCountryCode = CountryCodeArrayAdapter(
            context,
            R.layout.spinner_item,
            AppConstants.countryCodeList
        )
        with(spCountryCode) {
            this.adapter = adapterCountryCode
            setSelection(0, true)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    setMobileLength(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
        }

        setMobileLength(spCountryCode.selectedItemPosition)
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
    fun setMobileLength(position: Int) {
        mViewModel.minTextLength = AppConstants.countryCodeList[position].minLen
        mViewModel.maxTextLength = AppConstants.countryCodeList[position].maxLen
        Utility.setEditTextMaxLength(mViewBinding.etMobileNo, mViewModel.maxTextLength)
        mViewModel.selectedCountryCode.set(AppConstants.countryCodeList[position].dialCode)
    }

    /**
     * set observers for view model variables
     */
    private fun setObservers() {
        mViewModel.onMobileClicked.observe(this) {
            if (it) {
                requestPhoneNumberHint(this)
                mViewModel.onMobileClicked.value = false
            }
        }
        mViewModel.onContinueClicked.observe(this) {
            if (it) {
                mViewModel.callSendOtpApi()
                goToEnterOtpScreen()
                mViewModel.onContinueClicked.value = false
            }
        }
        mViewModel.onOtpSentSuccess.observe(this) {
            if (it) {
                goToEnterOtpScreen()
                mViewModel.onOtpSentSuccess.value = false
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
                        requestPhoneNumberHint(this)
                    }

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //requestPhoneNumberHint(this)
    }

    /**
     * Method to navigate to the Feeds screen after login
     */
    private fun goToEnterOtpScreen() {
        val intent = Intent(this, EnterOtpView::class.java)
        intent.putExtra(
            AppConstants.MOBILE_TYPE,
            mViewModel.selectedCountryCode.get() + mViewModel.mobile.value
        )
        intent.putExtra(
            AppConstants.MOBILE_WITHOUT_COUNTRY_CODE,
            mViewModel.mobile.value
        )
        startActivity(intent)
       // finish()
    }

    companion object {
        lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    }


}