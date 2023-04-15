package com.fypmoney.base

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.freshchat.consumer.sdk.FaqOptions
import com.freshchat.consumer.sdk.Freshchat
import com.fyp.trackr.models.*
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.util.*
import com.fypmoney.util.AppConstants.PLAY_STORE_URL
import com.fypmoney.util.dynamiclinks.DynamicLinksUtil.getInviteLinkWithExtraData
import com.fypmoney.util.dynamiclinks.DynamicLinksUtil.getInviteLinkWithNoData
import com.fypmoney.view.activity.LoginView
import com.fypmoney.view.register.TimeLineActivity
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.fypmoney.view.webview.WebViewActivity
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.toolbar.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors.newSingleThreadExecutor


/**
 *  Base Activity
 */
abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel> :
    BaseUpdateCheckActivity(), DialogUtils.OnAlertDialogNoInternetClickListener {
    private var dialog: Dialog? = null
    private var mViewDataBinding: T? = null
    private var mViewModel: V? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var executor: Executor
    private lateinit var biometricManager: BiometricManager
    private val TAG = BaseActivity::class.java.simpleName
    var navController: NavController? = null

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

                else -> {}
            }
        }
        mViewModel?.logoutUser?.observe(this)
        {
            if(it) {
                Utility.showToast(resources.getString(R.string.unauthrized_msg))
                Utility.resetPreferenceAfterLogout()
                val intent = Intent(this@BaseActivity , LoginView::class.java)
                    startActivity(intent)
                    finishAffinity()
            }
        }

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
                    getString(R.string.dialog_title_auth),
                    getString(R.string.dialog_msg_auth)
                )
                startActivityForResult(authIntent, AppConstants.DEVICE_SECURITY_REQUEST_CODE)

            }
        }else{
            callDeviceSecurity()
        }
    }

    /*
    * Ask for device security pin, pattern or fingerprint greater than OS pie
    * */
    private fun askForDeviceSecurity(executor: Executor, isFingerPrintAllowed: Boolean) {

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
                        RESULT_OK,
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
                        BiometricPrompt.ERROR_LOCKOUT -> {

                        }
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {

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
    @Deprecated("Deprecated in Java")
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
    * This method will set the lottie animation  with back  arrow
    * */
    fun setLottieAnimationToolBar(
        isBackArrowVisible: Boolean? = false, //back arrow for back press by default visibility of
        isLottieAnimation: Boolean? = false, // lottie animation by default on
        imageView: ImageView,
        lottieAnimationView: ImageView,
        toolbarTitle: String? = null,
        screenName: String
    )
    {
        // set back arrow visibility
        if (isBackArrowVisible == true)imageView.visibility=View.VISIBLE
        else imageView.visibility=View.GONE

        // set lottie animation visibility
        if (isLottieAnimation == true)lottieAnimationView.visibility=View.VISIBLE
        else lottieAnimationView.visibility=View.GONE

        imageView.setOnClickListener {
            finish()
        }

        lottieAnimationView.setOnClickListener {
            trackr {
                it.name = TrackrEvent.onboard_usertimeline_icon_click
                it.add(TrackrField.form_which_screen,screenName)
            }
            val intent = Intent(this@BaseActivity, TimeLineActivity::class.java)
            startActivity(intent)
        }
        if (toolbarTitle != null) {
            toolbar_title.visibility = View.VISIBLE
            toolbar_title.text = toolbarTitle
            toolbar_title.setTextColor(titleColor)

        }

    }


    /*
       * This is used to share the app
       * */
    fun inviteUser() {
        var content:String? = null
        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null
            && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {
            if (!SharedPrefUtils.getString(
                    applicationContext,
                    SharedPrefUtils.SF_REFFERAL_MSG_2
                ).isNullOrEmpty()
            ) {
                content = SharedPrefUtils.getString(
                        applicationContext,
                        SharedPrefUtils.SF_REFFERAL_MSG_2)

            } else {
                content = getString(
                    R.string.share_refral_code_34,
                    PLAY_STORE_URL
                )

            }

        } else {

            if (!SharedPrefUtils.getString(
                    applicationContext,
                    SharedPrefUtils.SF_REFFERAL_MSG
                ).isNullOrEmpty()
            ) {


                val code = Utility.getCustomerDataFromPreference()?.referralCode

                val redferMsg = SharedPrefUtils.getString(
                    applicationContext,
                    SharedPrefUtils.SF_REFFERAL_MSG
                )

                val contentWithCode =
                    code?.let { redferMsg?.replace(AppConstants.REFER_CODE_CHECKING_VARIABLE, it) }
               content = contentWithCode

            } else {
                content =
                    getString(
                        R.string.share_refral_code_34,
                        PLAY_STORE_URL
                    )
            }
        }
        if(Utility.getCustomerDataFromPreference()?.postKycScreenCode != null
            && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "0"){
            content?.let {
                Utility.getCustomerDataFromPreference()?.referralCode?.let { it1 ->
                    getInviteLinkWithExtraData(it,
                        it1
                    ) {
                        onInviteUser(it)
                    }
                }
            }
        }else{
            content?.let { onInviteUser(getInviteLinkWithNoData(it)) }
        }
    }


    fun shareInviteCodeFromReferal(content:String?){
        content.let {it->
            Utility.getCustomerDataFromPreference()?.referralCode?.let { it1 ->
                val contentWithCode = content?.replace(AppConstants.REFER_CODE_CHECKING_VARIABLE, it1)
                contentWithCode?.let { it2 ->
                    getInviteLinkWithExtraData(
                        it2,
                        it1
                    ) {
                        onInviteUser(it)
                    }
                }
            }
        }
    }
    fun onInviteUser(content: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, content)
        startActivity(Intent.createChooser(intent, "Share Link"))
    }

    override fun onTryAgainClicked() {

    }

    /*
  * This method is used to call fresh chat
  * */
    fun callFreshChat(context: Context) {
        val fresh = PockketApplication.instance.freshchat

        val user = fresh?.user?.apply {
            firstName = SharedPrefUtils.getString(
                application,
                SharedPrefUtils.SF_KEY_USER_FIRST_NAME
            )
            lastName = SharedPrefUtils.getString(
                application,
                SharedPrefUtils.SF_KEY_USER_LAST_NAME
            )

        }
        user?.setPhone(
            "+91",SharedPrefUtils.getString(
                application,
                SharedPrefUtils.SF_KEY_USER_MOBILE)

        )
        if (user != null) {
            fresh.user = user
        }
        val faqOptions = FaqOptions()
            .showFaqCategoriesAsGrid(true)
            .showContactUsOnAppBar(true)
            .showContactUsOnFaqScreens(true)
            .showContactUsOnFaqNotHelpful(true)
        Freshchat.showFAQs(context, faqOptions)
    }

    private fun callDeviceSecurity() {
        if(!supportFragmentManager.isDestroyed){
            val bottomSheet = DeviceSecurityWarningBottomSheet(setDeviceSecurity={
                val intent = Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD)
                startActivity(intent)
            })
            bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            bottomSheet.show(supportFragmentManager, "device_security")
        }

    }

     fun openWebPageFor(title: String, url: String) {
        val intent = Intent(this@BaseActivity, WebViewActivity::class.java)
        intent.putExtra(ARG_WEB_URL_TO_OPEN, url)
        intent.putExtra(ARG_WEB_PAGE_TITLE, title)
        startActivity(intent)
    }

    fun shareLink(url: String){
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            url
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }


    /**
     * Method to navigate to the different activity
     */
    fun intentToActivityMain(context: Context,aClass: Class<*>, isFinishAll: Boolean? = false) {
        val intent = Intent(context, aClass)
        startActivity(intent)
        if (isFinishAll == true) {
            finishAffinity()
        }
    }
}