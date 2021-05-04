package com.dreamfolks.baseapp.base

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.dreamfolks.baseapp.BuildConfig
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.application.PockketApplication
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.util.DialogUtils
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.toolbar.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors.newSingleThreadExecutor


/**
 *  Base Activity
 */
abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel> :
    AppCompatActivity(),DialogUtils.OnAlertDialogNoInternetClickListener {
    private var dialog: Dialog? = null
    private var mViewDataBinding: T? = null
    private var mViewModel: V? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var executor: Executor
    private lateinit var biometricManager: BiometricManager
    val PERMISSION_READ_CONTACTS = 1
    val PERMISSION_WRITE_EXTERNAL_STORAGE = 2


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
                    DialogUtils.showInternetErrorDialog(this,this)
                    mViewModel!!.internetError.value = false

                }

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
        firebaseAnalytics.logEvent("dreamFolks_login", params)

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val km =
                applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

            if (km.isKeyguardSecure) {
                val authIntent = km.createConfirmDeviceCredentialIntent(
                    AppConstants.DIALOG_TITLE_AUTH,
                    AppConstants.DIALOG_MSG_AUTH
                )
                ActivityCompat.startActivityForResult(
                    this,
                    authIntent,
                    AppConstants.DEVICE_SECURITY_REQUEST_CODE,
                    null
                )
            }
        } else {
            askForDeviceSecurity(executor)

        }
    }

    /*
      * Ask for device security pin, pattern or fingerprint greater than OS pie
      * */
    private fun askForDeviceSecurity(executor: Executor) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(AppConstants.DIALOG_TITLE_AUTH)
            .setDescription(AppConstants.DIALOG_MSG_AUTH)
            .setDeviceCredentialAllowed(true)
            .build()
        // 1
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                // 2
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    onActivityResult(AppConstants.DEVICE_SECURITY_REQUEST_CODE, RESULT_OK, Intent())
                    super.onAuthenticationSucceeded(result)

                }

                // 3
                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
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
    fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, READ_CONTACTS)
        return result == PackageManager.PERMISSION_GRANTED
    }

    /*
    * This method ask for permission
    * */
    fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(READ_CONTACTS),
            AppConstants.READ_CONTACTS_PERMISSION_CODE
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
            AppConstants.READ_CONTACTS_PERMISSION_CODE
        )
    }

    /*
     * This method will set the toolbar with back navigation arrow and toolbar title
     * */
    fun setToolbarAndTitle(
        context: Context,
        toolbar: Toolbar,
        textView: AppCompatTextView,
        toolbarTitle: String? = null,
        isBackArrowVisible: Boolean? = false
    ) {
        setSupportActionBar(toolbar)
        val upArrow = ContextCompat.getDrawable(
            context,
            R.drawable.arrow_back
        )

        supportActionBar?.let {
            if (isBackArrowVisible!!) {
                it.setHomeAsUpIndicator(upArrow)
                it.setDisplayHomeAsUpEnabled(true)
            }
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayShowTitleEnabled(false)
            textView.text = toolbarTitle
        }

    }

    /*
       * This is used to share the app
       * */
    fun inviteUser() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    override fun onTryAgainClicked() {

    }

}