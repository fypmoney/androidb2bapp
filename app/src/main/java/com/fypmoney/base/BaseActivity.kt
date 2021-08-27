package com.fypmoney.base

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.freshchat.consumer.sdk.FaqOptions
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatConfig
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.CASHBACK_AMOUNT
import com.fypmoney.util.AppConstants.PLAY_STORE_URL
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.LoginView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.toolbar.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors.newSingleThreadExecutor


/**
 *  Base Activity
 */
abstract class
BaseActivity<T : ViewDataBinding, V : BaseViewModel> :
    BaseUpdateCheckActivity(), DialogUtils.OnAlertDialogNoInternetClickListener {
    private var dialog: Dialog? = null
    private var mViewDataBinding: T? = null
    private var mViewModel: V? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var executor: Executor
    private lateinit var biometricManager: BiometricManager
    val PERMISSION_READ_CONTACTS = 1
    val PERMISSION_WRITE_EXTERNAL_STORAGE = 2
    private val TAG = BaseActivity::class.java.simpleName

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract fun getBindingVariable(): Int

    /**
     * @return layout resource id
     */
    abstract fun getLayoutId(): Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */

    abstract fun getViewModel(): V

    fun getViewDataBinding(): T {
        return mViewDataBinding!!
    }


    override fun onStart() {
        super.onStart()
        if(PockketApplication.instance.appUpdateRequired){
             SharedPrefUtils.getInt(
                applicationContext,
                SharedPrefUtils.SF_KEY_APP_UPDATE_TYPE
            )?.let {
                 updateType = it
                 checkForAppUpdate()
                 PockketApplication.instance.appUpdateRequired = false
            }

        }
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executor = newSingleThreadExecutor()
        biometricManager = BiometricManager.from(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        dialog = Dialog(this)
        performDataBinding()
        setObservers()
    }


    override fun onResume() {
        super.onResume()
        onUserInteraction()
    }

    private fun performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mViewModel = when (this.mViewModel) {
            null -> getViewModel()
            else -> mViewModel
        }

        mViewDataBinding?.setVariable(getBindingVariable(), mViewModel)
        mViewDataBinding?.executePendingBindings()
        mViewDataBinding?.lifecycleOwner = this
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            onUserInteraction()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchGenericMotionEvent(ev: MotionEvent?): Boolean {
        onUserInteraction()
        return super.dispatchGenericMotionEvent(ev)
    }

    override fun dispatchTrackballEvent(ev: MotionEvent?): Boolean {
        onUserInteraction()
        return super.dispatchTrackballEvent(ev)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        onUserInteraction()
        return super.dispatchKeyEvent(event)
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean {
        onUserInteraction()
        return super.dispatchKeyShortcutEvent(event)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    open fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    open fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /*
    * Observer to observe the live data
    * */
    private fun setObservers() {
        mViewModel?.progressDialog?.observe(this)
        {
            when (it) {
                true -> {
                    DialogUtils.showProgressDialog(this)
                }
                false -> {
                    DialogUtils.dismissProgressDialog()
                }
            }
        }

        mViewModel?.internetError?.observe(this)
        {
            when (it) {
                true -> {
                    DialogUtils.showInternetErrorDialog(this, this)
                    mViewModel!!.internetError.value = false

                }

            }
        }
        mViewModel?.logoutUser?.observe(this)
        {
            if(it) {
                Utility.showToast(resources.getString(R.string.unauthrized_msg))
                val intent = Intent(this@BaseActivity , LoginView::class.java)
                    startActivity(intent)
                    finishAffinity()
            }
        }

    }

    /*
    * This method will insert the firebase analytics Logs
    * */
    fun insertAnalyticsLogs() {
        val params = Bundle()
        params.putString("button_name", "Login")
        params.putString("button_text", "Login clicked")
        firebaseAnalytics.logEvent("fyp_login", params)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "100")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "login_button")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


    }

    /*
   * Ask for device security pin, pattern or fingerprint
   * */
    fun askForDevicePassword() {
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
        if (km!!.isKeyguardSecure) {
            if(packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
                askForDeviceSecurity(executor,true)
            }else{
                val authIntent = km.createConfirmDeviceCredentialIntent(
                    getString(com.fypmoney.R.string.dialog_title_auth),
                    getString(R.string.dialog_msg_auth)
                )
                startActivityForResult(authIntent, AppConstants.DEVICE_SECURITY_REQUEST_CODE)

            }

        }
    }

    /*
    * Ask for device security pin, pattern or fingerprint greater than OS pie
    * */
    fun askForDeviceSecurity(executor: Executor, isFingerPrintAllowed: Boolean) {

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(AppConstants.DIALOG_TITLE_AUTH)
            .setDescription(AppConstants.DIALOG_MSG_AUTH)
            .setAllowedAuthenticators(if(!isFingerPrintAllowed){
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            }else{
                BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            })
            .build()
        // 1
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                // 2
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    onActivityResult(
                        AppConstants.DEVICE_SECURITY_REQUEST_CODE,
                        AppCompatActivity.RESULT_OK,
                        Intent()
                    )
                    super.onAuthenticationSucceeded(result)
                }

                // 3
                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    when(errorCode){
                        BiometricPrompt.ERROR_HW_NOT_PRESENT->{

                        }
                        BiometricPrompt.ERROR_CANCELED -> {

                        }
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                        }
                        BiometricPrompt.ERROR_LOCKOUT or BiometricPrompt.ERROR_LOCKOUT_PERMANENT  -> {
                            //Too Many Attempts, plese try after some time.
                        }
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {

                        }
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                        }
                        BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
                        }
                        BiometricPrompt.ERROR_NO_SPACE -> {
                        }
                        BiometricPrompt.ERROR_TIMEOUT -> {
                        }
                        BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> {
                        }
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                        }
                        BiometricPrompt.ERROR_VENDOR -> {
                        }
                    }
                    Log.d(TAG,"Authentication error with $errorCode and $errString")
                    /**/
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
                    Log.d(TAG,"onAuthenticationFailed")
                    super.onAuthenticationFailed()
                }
            })
        biometricPrompt.authenticate(promptInfo)
    }




    // call back when password is correct or incorrect
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.DEVICE_SECURITY_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                }
                RESULT_CANCELED -> {
                    finishAffinity()
                }

            }
        }
    }

    /*
    * This method is used to check if permission is granted or not
    * */
    fun checkPermission(permission: String): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    /*
    * This method ask for permission
    * */
    fun requestPermission(permission: String) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            AppConstants.PERMISSION_CODE
        )
    }

    /*
   * This method is used to check if permission is granted or not
   * */
    fun checkLocationPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, ACCESS_FINE_LOCATION)
        val result1 =
            ContextCompat.checkSelfPermission(applicationContext, ACCESS_BACKGROUND_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    /*
    * This method ask for permission
    * */
    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION),
            AppConstants.PERMISSION_CODE
        )
    }

    /*
     * This method will set the toolbar with back navigation arrow and toolbar title
     * */
    fun setToolbarAndTitle(
        context: Context,
        toolbar: Toolbar,
        isBackArrowVisible: Boolean? = false, toolbarTitle: String? = null,backArrowTint:Int = Color.BLACK,
        titleColor:Int = Color.BLACK
    ) {
        setSupportActionBar(toolbar)
        val upArrow = ContextCompat.getDrawable(
            context,
            R.drawable.ic_back_new
        )
        upArrow?.setTint(backArrowTint)

        supportActionBar?.let {
            if (isBackArrowVisible!!) {
                it.setHomeAsUpIndicator(upArrow)
                it.setDisplayHomeAsUpEnabled(true)
            }
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayShowTitleEnabled(false)
            if (toolbarTitle != null) {
                toolbar_title.text = toolbarTitle
                toolbar_title.setTextColor(titleColor)

            }
        }

    }


    /*
       * This is used to share the app
       * */
    fun inviteUser() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.share_refral_code,
                Utility.getCustomerDataFromPreference()?.referralCode,CASHBACK_AMOUNT,PLAY_STORE_URL
            )
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    override fun onTryAgainClicked() {

    }

    /**
     * @param context
     * @return true if pass or pin set
     */
    fun isPassOrPinSet(): Boolean {
        val keyguardManager =
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager //api 16+
        return keyguardManager.isKeyguardSecure
    }

    /*
  * This method is used to call fresh chat
  * */
    fun callFreshChat(context: Context) {
        val fresh = Freshchat.getInstance(context)
        val config = FreshchatConfig(
            AppConstants.FRESH_CHAT_APP_ID,
            AppConstants.FRESH_CHAT_APP_KEY
        )
        config.domain = AppConstants.FRESH_CHAT_DOMAIN
        config.isCameraCaptureEnabled = true
        config.isGallerySelectionEnabled = true
        config.isResponseExpectationEnabled = true
        config.isTeamMemberInfoVisible = true
        config.isUserEventsTrackingEnabled = true
        fresh.init(config)
        val faqOptions = FaqOptions()
            .showFaqCategoriesAsGrid(false)
            .showContactUsOnAppBar(true)
            .showContactUsOnFaqScreens(true)
            .showContactUsOnFaqNotHelpful(true)
        Freshchat.showFAQs(context, faqOptions)
    }

}