package com.fypmoney.view


import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BuildConfig
import com.fypmoney.R
import com.fypmoney.model.CardInfoDetailsBottomSheet
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.CardDetailsBottomSheet
import com.fypmoney.viewmodel.CardDetailsViewModel
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class StoreWebpageOpener : AppCompatActivity() {
    private var card: CardInfoDetailsBottomSheet? = null
    private var executor: ExecutorService? = null
    private var mViewModel: CardDetailsViewModel? = null
    private var load_progress: ImageView? = null
    private var webView: WebView? = null
    private val TAG = StoreWebpageOpener::class.java.simpleName
    companion object {
        var url = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        var title = intent.getStringExtra("title")

        if (title != null) {
            title_tv.text = title
        }
        mViewModel = ViewModelProvider(this).get(CardDetailsViewModel::class.java)
        executor = Executors.newSingleThreadExecutor()
        load_progress = findViewById<ImageView>(R.id.load_progress_bar)
        webView = findViewById<WebView>(R.id.webView1)
        window.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView!!.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)
        }
        webView!!.webViewClient = CustomWebViewClient()
        webView!!.settings.javaScriptEnabled = true
        webView!!.settings.domStorageEnabled = true
        webView!!.settings.allowFileAccess = true;
        webView!!.setInitialScale(1)
        webView!!.settings.loadWithOverviewMode = true
        webView!!.settings.useWideViewPort = true
        webView!!.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK;

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                title = "Loading..."
                if (progress == 100) load_progress_bar.visibility = View.GONE
            }

        }
        mViewModel?.availableAmount?.observe(
            this,
            { amount ->
                CoroutineScope(Dispatchers.Main).launch {
                    amount_tv.text = " ₹" + amount
                }

            })
        mViewModel?.carddetails?.observe(
            this,
            { carddetails ->
                CoroutineScope(Dispatchers.Main).launch {
                    card = carddetails
                }

            })

        card_details.setOnClickListener {
            if (mViewModel?.carderror?.get() == true) {

                Utility.showToast(mViewModel?.carderrormsg?.get())

            } else {
                if (card != null) {
                    askForDevicePassword()
                } else {
                    Toast.makeText(this, "Fetching card details", Toast.LENGTH_SHORT).show()
                }

            }

        }



        webView!!.loadUrl(url)

        toolbar_backImage.setOnClickListener {
            onBackPressed()
        }
        refresh.setOnClickListener({

            webView?.reload()

        })


    }
    /*
  * Ask for device security pin, pattern or fingerprint
  * */
    fun askForDevicePassword() {
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
        if (km!!.isKeyguardSecure) {
            if(packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
                executor?.let { askForDeviceSecurity(it,true) }
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




    private fun callCardSettingsBottomSheet() {
        val bottomSheet =
            CardDetailsBottomSheet(card)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "CardSettings")
    }
    open class CustomWebViewClient() : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }



        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

          //
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        callCardSettingsBottomSheet()

                    }

                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView!!.canGoBack()) {
            webView!!.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}