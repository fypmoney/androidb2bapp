package com.fypmoney.view.discord


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityWebviewDiscordBinding
import com.fypmoney.util.AdvancedWebView
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.discord.viewmodel.DiscordWebConnectVM
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.coroutines.delay


class DiscordWebView : BaseActivity<ActivityWebviewDiscordBinding, DiscordWebConnectVM>(),
    AdvancedWebView.Listener {

    private lateinit var mViewModel: DiscordWebConnectVM

    private lateinit var binding: ActivityWebviewDiscordBinding


    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_webview_discord

    override fun getViewModel(): DiscordWebConnectVM {
        mViewModel = ViewModelProvider(this).get(DiscordWebConnectVM::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        val url = intent?.getStringExtra(ARG_WEB_URL_TO_OPEN)
        val pageTitle = intent?.getStringExtra(ARG_WEB_PAGE_TITLE)
        if (title != null) {
            binding.titleToolbar.text = pageTitle
        }

        binding.webView1.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                title = "Loading..."
                if (progress == 100) load_progress_bar.visibility = View.GONE
            }
        }

        binding.webView1.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {


                if (url.contains("/discord/failed_redirect_url")) {
                    callDiscordConnectionFailSheet()
                } else if (url.contains("/discord/success_redirect_url")) {
                    SharedPrefUtils.putString(
                        application, key = SharedPrefUtils.SF_DICORD_CONNECTED,
                        value = "connected"
                    )
                    // your code to start second activity. Will wait for 3 seconds before calling this method
                    lifecycleScope.launchWhenResumed {
                        delay(1200)
                        val intent = Intent(this@DiscordWebView, DiscordProfileActivity::class.java)
                        startActivity(intent)
                        finish()
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
        } else {
            Utility.showToast(getString(R.string.unable_to_open_page_please_try_again_later))
        }


        binding.toolbarBackImage.setOnClickListener {
            onBackPressed()
        }


    }

    private fun callDiscordConnectionFailSheet() {

        val bottomSheet =
            DiscordBottomSheet(
                finishDiscord = {
                    finish()
                }

            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "DiscordBottomSheet")
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView1.canGoBack()) {
            binding.webView1.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
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

    }

    override fun onExternalPageRequest(url: String?) {
        startActivity(
            Intent.createChooser(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                ), getString(R.string.browse_with)
            )
        )
    }


}