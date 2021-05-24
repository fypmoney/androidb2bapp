package com.fypmoney.base

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.fypmoney.R
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import java.util.concurrent.Executor
import java.util.concurrent.Executors


//create base fragment with AndroidViewModel

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : Fragment(),DialogUtils.OnAlertDialogNoInternetClickListener {

    private var baseActivity: BaseActivity<*, *>? = null
        private set
    private var mRootView: View? = null
    private var viewDataBinding: T? = null
    private var mViewModel: V? = null
    private lateinit var executor: Executor


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
   * Observer to observe the live data
   * */
    private fun setObservers() {
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
                    DialogUtils.showInternetErrorDialog(requireActivity(),this)
                    mViewModel!!.internetError.value = false

                }

            }
        }

    }

    /*
  * This method will set the toolbar with back navigation arrow and toolbar title
  * */
    fun setToolbarAndTitle(
        context: Context,
        toolbar: Toolbar,
        isBackArrowVisible: Boolean? = false
    ) {  val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val upArrow = ContextCompat.getDrawable(
            context,
            R.drawable.ic_back_arrow
        )

        activity.supportActionBar?.let {
            if (isBackArrowVisible!!) {
                it.setHomeAsUpIndicator(upArrow)
                it.setDisplayHomeAsUpEnabled(true)
            }
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

    }
    /*
   * Ask for device security pin, pattern or fingerprint
   * */
    fun askForDevicePassword() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val km =
                requireContext().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

            if (km.isKeyguardSecure) {
                val authIntent = km.createConfirmDeviceCredentialIntent(
                    AppConstants.DIALOG_TITLE_AUTH,
                    AppConstants.DIALOG_MSG_AUTH
                )
                ActivityCompat.startActivityForResult(
                    requireActivity(),
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
    fun askForDeviceSecurity(executor: Executor) {
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
                    onActivityResult(AppConstants.DEVICE_SECURITY_REQUEST_CODE, AppCompatActivity.RESULT_OK, Intent())
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
                AppCompatActivity.RESULT_OK -> {
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    finishAffinity(requireActivity())
                }

            }
        }
    }

}
