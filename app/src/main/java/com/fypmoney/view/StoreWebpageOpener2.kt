package com.fypmoney.view


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityWebview2Binding
import com.fypmoney.model.CardInfoDetailsBottomSheet
import com.fypmoney.util.AdvancedWebView
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.CardDetailsBottomSheet
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.fypmoney.viewmodel.CardDetailsViewModel
import kotlinx.android.synthetic.main.activity_webview2.*
import java.net.URISyntaxException



class StoreWebpageOpener2 : BaseActivity<ActivityWebview2Binding, CardDetailsViewModel>(),
    AdvancedWebView.Listener {

    private var card: CardInfoDetailsBottomSheet? = null
    private lateinit var mViewModel: CardDetailsViewModel
    private val TAG = StoreWebpageOpener2::class.java.simpleName
    private lateinit var binding: ActivityWebview2Binding



    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_webview2

    override fun getViewModel(): CardDetailsViewModel {
        mViewModel = ViewModelProvider(this).get(CardDetailsViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        val url = intent?.getStringExtra(ARG_WEB_URL_TO_OPEN)
        val pageTitle = intent?.getStringExtra(ARG_WEB_PAGE_TITLE)
        if (title != null) {
            title_tv.text = pageTitle
        }

        binding.webView1

        binding.webView1.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                title = "Loading..."
                if (progress == 100) load_progress_bar.visibility = View.GONE
            }
        }

        binding.webView1.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("intent://")) {
                    try {
                        val context: Context = view.context
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        if (intent != null) {
                            view.stopLoading()
                            val packageManager: PackageManager = context.packageManager
                            val info = packageManager.resolveActivity(
                                intent,
                                PackageManager.MATCH_DEFAULT_ONLY
                            )
                            if (info != null) {
                                context.startActivity(intent)
                            } else {
                                val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                view.loadUrl(fallbackUrl!!)
                            }
                            return true
                        }
                    } catch (e: URISyntaxException) {
                            Log.e(TAG, "Can't resolve intent://", e)

                    }
                }
                return false
            }
        }


        binding.webView1.setListener(this, this)
        binding.webView1.setMixedContentAllowed(false)

        binding.webView1.setCookiesEnabled(true)
        binding.webView1.settings.setSupportMultipleWindows(true)
        binding.webView1.settings.javaScriptCanOpenWindowsAutomatically = true
        if (url != null) {
            binding.webView1.loadUrl(url)
        }else{
            Utility.showToast(getString(R.string.unable_to_open_page_please_try_again_later))
        }

        mViewModel.availableAmount.observe(
            this,
            { amount ->
                //amount_tv.text = " ₹" + amount
                amount_tv.text = getString(R.string.fyp_card_details)
            })
        mViewModel.carddetails.observe(
            this,
            { carddetails ->
                card = carddetails
            })

        binding.cardDetails.setOnClickListener {
            if (mViewModel.carderror.get() == true) {
                Utility.showToast(mViewModel.carderrormsg.get())
            } else {
                if (card != null) {
                    askForDevicePassword()
                } else {
                    Toast.makeText(this, "Fetching card details", Toast.LENGTH_SHORT).show()
                }

            }

        }

        binding.toolbarBackImage.setOnClickListener {
            onBackPressed()
        }


    }


    private fun callCardSettingsBottomSheet() {
        val bottomSheet =
            CardDetailsBottomSheet(card)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheet.show(supportFragmentManager, "CardSettings")
    }




    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView1.canGoBack()) {
            binding.webView1.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
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

    override fun onPageStarted(url: String?, favicon: Bitmap?) {

    }

    override fun onPageFinished(url: String?) {

    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {

    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun onExternalPageRequest(url: String?) {
        TODO("Not yet implemented")
    }

    fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        return if (url == null || url.startsWith("http://") || url.startsWith("https://")) false else try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            view.context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.i(TAG, "shouldOverrideUrlLoading Exception:$e")
            true
        }
    }

}