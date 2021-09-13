package com.fypmoney.view


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.BuildConfig
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityWebviewBinding
import com.fypmoney.model.CardInfoDetailsBottomSheet
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.CardDetailsBottomSheet
import com.fypmoney.viewmodel.CardDetailsViewModel
import kotlinx.android.synthetic.main.activity_webview.*


class StoreWebpageOpener : BaseActivity<ActivityWebviewBinding, CardDetailsViewModel>() {
    private var card: CardInfoDetailsBottomSheet? = null
    private lateinit var mViewModel: CardDetailsViewModel
    private val TAG = StoreWebpageOpener::class.java.simpleName
    private lateinit var binding: ActivityWebviewBinding

    companion object {
        var url = ""
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_webview

    override fun getViewModel(): CardDetailsViewModel {
        mViewModel = ViewModelProvider(this).get(CardDetailsViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        var title = intent.getStringExtra("title")

        if (title != null) {
            title_tv.text = title
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.webView1.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)
        }
        binding.webView1.webViewClient = CustomWebViewClient()
        binding.webView1.settings.javaScriptEnabled = true
        binding.webView1.settings.domStorageEnabled = true
        binding.webView1.settings.allowFileAccess = true;
        binding.webView1.setInitialScale(1)
        binding.webView1.settings.loadWithOverviewMode = true
        binding.webView1.settings.useWideViewPort = true
        binding.webView1.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK;

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        binding.webView1.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                title = "Loading..."
                if (progress == 100) load_progress_bar.visibility = View.GONE
            }

        }
        mViewModel.availableAmount.observe(
            this,
            { amount ->
                amount_tv.text = " ₹" + amount
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
        binding.webView1.loadUrl(url)

        binding.toolbarBackImage.setOnClickListener {
            onBackPressed()
        }
        refresh.setOnClickListener {
            binding.webView1.reload()

        }


    }


    private fun callCardSettingsBottomSheet() {
        val bottomSheet =
            CardDetailsBottomSheet(card)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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

}