package com.dreamfolks.baseapp.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.ViewModelProvider
import com.dreamfolks.baseapp.BR
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.base.BaseActivity
import com.dreamfolks.baseapp.databinding.ViewLoginBinding
import com.dreamfolks.baseapp.receivers.SmsBroadcastReceiver
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.util.Utility
import com.dreamfolks.baseapp.view.adapter.CountryCodeArrayAdapter
import com.dreamfolks.baseapp.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsClient
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.toolbar.*

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
            textView = toolbar_title,
            toolbarTitle = getString(R.string.sign_in_sign_up)
        )
        requestPhoneNumberHint(this)

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
            android.R.layout.simple_spinner_item,
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
        finish()
    }

    companion object {
        lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    }


}