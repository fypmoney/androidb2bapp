package com.fypmoney.view.discord

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import com.fypmoney.R
import kotlinx.android.synthetic.main.activity_webview.*

const val ARG_WEB_URL_TO_OPEN = "web_url_to_open"
const val ARG_WEB_PAGE_TITLE = "web_page_title"


class DiscordWebViewActivity : AppCompatActivity() {

    private var load_progress: ImageView? = null
    private var webView: WebView? = null
    private var toolbarTitle: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        var url = intent?.getStringExtra(ARG_WEB_URL_TO_OPEN)
        val pageTitle = intent?.getStringExtra(ARG_WEB_PAGE_TITLE)
//        url="http://3.91.202.15/discord/success_redirect_url/"

        load_progress = findViewById(R.id.load_progress_bar)
        toolbarTitle = findViewById(R.id.titleToolbar)
        card_details.visibility = View.GONE
        webView = findViewById(R.id.webView1)
        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                title = "Loading..."
                if (progress == 100) load_progress_bar.visibility = View.GONE
            }

        }


        refresh.setOnClickListener {
            webView?.reload()
        }


        toolbar_backImage.setOnClickListener {
            onBackPressed()
        }

        initWebView(url ?: "", pageTitle ?: "")
    }

    private fun initWebView(url: String, title: String) {
        toolbarTitle?.text = title
        webView!!.clearHistory()



        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("/discord/failed_redirect_url")) {
                    callDicordConnectionFailSheet()
                } else if (url.contains("/discord/success_redirect_url")) {
                    val intent =
                        Intent(this@DiscordWebViewActivity, DiscordProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                // load_progress_bar.clearAnimation()
                load_progress_bar.visibility = View.GONE
            }

        }



        webView1.setMixedContentAllowed(false)

        webView1.setCookiesEnabled(true)
        webView1.settings.setSupportMultipleWindows(true)
        webView1.settings.javaScriptCanOpenWindowsAutomatically = true

        webView!!.loadUrl(url)
    }

    private fun callDicordConnectionFailSheet() {

        val bottomSheet =
            DiscordBottomSheet(this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "TransactionFail")
    }

}