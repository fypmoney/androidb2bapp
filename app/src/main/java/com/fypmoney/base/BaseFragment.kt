package com.fypmoney.base


import android.Manifest
import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.freshchat.consumer.sdk.ConversationOptions
import com.freshchat.consumer.sdk.FaqOptions
import com.freshchat.consumer.sdk.Freshchat
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.DEVICE_SECURITY_REQUEST_CODE
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.LoginView
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.delay
import java.util.concurrent.Executor
import java.util.concurrent.Executors


//create base fragment with AndroidViewModel

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : Fragment(),
    DialogUtils.OnAlertDialogNoInternetClickListener {

    private var baseActivity: BaseActivity<*, *>? = null
        private set
    private var mRootView: View? = null
    private var viewDataBinding: T? = null
    private var mViewModel: V? = null
    private lateinit var executor: Executor
    private val TAG = BaseFragment::class.java.simpleName

    fun getViewDataBinding(): T {
        return viewDataBinding!!
    }

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mViewModel = if (this.mViewModel == null)
            getViewModel() else this.mViewModel
        setHasOptionsMenu(false)
        executor = Executors.newSingleThreadExecutor()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mRootView = viewDataBinding!!.root
        return mRootView
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(getBindingVariable(), mViewModel)
        viewDataBinding!!.lifecycleOwner = this
        viewDataBinding!!.executePendingBindings()
        setObservers()
    }
    /*
     * This method is used to check if permission is granted or not
     * */
    fun checkLocationPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val result1 =
            ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        val result2 =
            ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED&&result2 == PackageManager.PERMISSION_GRANTED
    }

    /*
    * This method ask for permission
    * */
    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            AppConstants.PERMISSION_CODE
        )
    }

    /*
   * Observer to observe the live data
   * */
    private fun setObservers() {
        mViewModel?.alertDialog?.observe(viewLifecycleOwner)
        {
            if(viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                it?.let {
                    DialogUtils.showSuccessAndErrorDialog(requireActivity(),
                        alertStateUiModel = it)
                }?: kotlin.run {
                    mViewModel?.alertDialog?.value = null
                }
            }


        }
        mViewModel?.logoutUser?.observe(viewLifecycleOwner)
        {
            if(it) {
                Utility.showToast(resources.getString(R.string.unauthrized_msg))
                Utility.resetPreferenceAfterLogout()
                val intent = Intent(requireActivity() , LoginView::class.java)
                startActivity(intent)
                requireActivity().finishAffinity()

            }
        }
        mViewModel?.progressDialog?.observe(viewLifecycleOwner)
        {
            when (it) {
                true -> {
                    DialogUtils.showProgressDialog(requireActivity())
                }
                false -> {
                    DialogUtils.dismissProgressDialog()
                }
            }
        }

        mViewModel?.internetError?.observe(viewLifecycleOwner)
        {
            when (it) {
                true -> {
                    DialogUtils.showInternetErrorDialog(requireActivity(), this)
                    mViewModel!!.internetError.value = false

                }

                else -> {}
            }
        }

    }

    /*
  * This method will set the toolbar with back navigation arrow and toolbar title
  * */
    fun setToolbarAndTitle(
        context: Context,
        toolbar: Toolbar,
        isBackArrowVisible: Boolean? = false, toolbarTitle: String? = null,
        titleColor:Int = Color.WHITE,
        backArrowTint:Int = Color.WHITE
    ) {
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val upArrow = ContextCompat.getDrawable(
            context,
            R.drawable.ic_back_new
        )

        upArrow?.setTint(backArrowTint)

        activity.supportActionBar?.let {
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

    fun setToolbarBackgroundAndTitle(
        context: Context,
        toolbar: Toolbar,
        isBackArrowVisible: Boolean? = false, toolbarTitle: String? = null,
        titleColor:Int = Color.WHITE,
        backArrowTint:Int = Color.WHITE
    ) {
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val upArrow = ContextCompat.getDrawable(
            context,
            R.drawable.ic_back_new
        )

        upArrow?.setTint(backArrowTint)

        activity.supportActionBar?.let {
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

    internal open fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, which ->
            dialog.cancel()
            mViewModel?.alertDialog?.value = DialogUtils.AlertStateUiModel(
                icon = ContextCompat.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_error_alert
                )!!,
                message = "Please allow the permissions to use this feature.",
                backgroundColor = ContextCompat.getColor(
                    PockketApplication.instance,
                    R.color.errorAlertBgColor
                )
            )
            lifecycleScope.launchWhenResumed {
                delay(DialogUtils.alertDialogTime+100)
                findNavController().navigateUp()
            }
        }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    /*
   * Ask for device security pin, pattern or fingerprint
   * */
    fun askForDevicePassword() {
        val km = requireContext().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
        if (km!!.isKeyguardSecure) {
            if(requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
                askForDeviceSecurity(executor,true)
            }else{
                val authIntent = km.createConfirmDeviceCredentialIntent(
                    getString(com.fypmoney.R.string.dialog_title_auth),
                    getString(R.string.dialog_msg_auth)
                )
                startActivityForResult(authIntent, DEVICE_SECURITY_REQUEST_CODE)

            }

        }else{
            callDeviceSecurity()
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
                    DEVICE_CREDENTIAL
                }else{
                    BIOMETRIC_WEAK or DEVICE_CREDENTIAL
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
                        DEVICE_SECURITY_REQUEST_CODE,
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
        if (requestCode == DEVICE_SECURITY_REQUEST_CODE) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {

                }
                AppCompatActivity.RESULT_CANCELED -> {
                    finishAffinity(requireActivity())
                }

            }
        }
    }

    private fun callDeviceSecurity() {
            val bottomSheet = DeviceSecurityWarningBottomSheet(setDeviceSecurity={
                val intent = Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD)
                startActivity(intent)
            })
            bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            bottomSheet.show(requireActivity().supportFragmentManager, "device_security")
    }


    fun callFreshChat(context: Context) {
        val fresh = PockketApplication.instance.freshchat

        val user = fresh?.user?.apply {
            firstName = SharedPrefUtils.getString(
                requireActivity().applicationContext,
                SharedPrefUtils.SF_KEY_USER_FIRST_NAME
            )
            lastName = SharedPrefUtils.getString(
                requireActivity().applicationContext,
                SharedPrefUtils.SF_KEY_USER_LAST_NAME
            )

        }
        user?.setPhone(
            "+91", SharedPrefUtils.getString(
                requireActivity().applicationContext,
                SharedPrefUtils.SF_KEY_USER_MOBILE)

        )
        if (user != null) {
            fresh.user = user
        }

        val tags: MutableList<String> = ArrayList()
        tags.add("kyc_agent_app")
        val faqOptions = FaqOptions()
            .showFaqCategoriesAsGrid(true)
            .showContactUsOnAppBar(true)
            .showContactUsOnFaqScreens(true)
            .showContactUsOnFaqNotHelpful(true)
            .filterContactUsByTags(tags,"Fyp KYC Agent")
        faqOptions.filterByTags(tags,"Fyp KYC agent",FaqOptions.FilterType.CATEGORY)
        Freshchat.showFAQs(context, faqOptions)
    }

}
