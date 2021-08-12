package com.fypmoney.view


import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.R
import com.fypmoney.model.CardInfoDetailsBottomSheet
import com.fypmoney.util.AppConstants
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

    companion object {
        var url = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_PROGRESS);
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
        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                title = "Loading..."
//                setProgress(progress * 100) //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if (progress == 100) load_progress_bar.visibility = View.GONE
            }

        }

        mViewModel?.availableAmount?.observe(
            this,
            androidx.lifecycle.Observer { amount ->
                CoroutineScope(Dispatchers.Main).launch {
                    amount_tv.text = " ₹" + amount
                }

            })
        mViewModel?.carddetails?.observe(
            this,
            androidx.lifecycle.Observer { amount ->
                CoroutineScope(Dispatchers.Main).launch {
                  card=amount
                }

            })

        card_details.setOnClickListener {

            askForDevicePassword()
        }
        webView!!.webViewClient = CustomWebViewClient()
        webView!!.settings.javaScriptEnabled = true



        webView!!.loadUrl(url)

        toolbar_backImage.setOnClickListener {
            onBackPressed()
        }
        refresh.setOnClickListener(View.OnClickListener {

            webView?.reload()

        })


    }
    fun askForDevicePassword() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val km =
                getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

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
            askForDeviceSecurity(executor!!)

        }
    }
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
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
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
                    AppCompatActivity.RESULT_OK -> {
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