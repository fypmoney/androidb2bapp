package com.fypmoney.view.webview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.fypmoney.R
import com.fypmoney.view.StoreWebpageOpener
import kotlinx.android.synthetic.main.activity_webview.*

const val ARG_WEB_URL_TO_OPEN = "web_url_to_open"
const val ARG_WEB_PAGE_TITLE = "web_page_title"


class WebViewActivity : AppCompatActivity() {

    private var load_progress: ImageView?=null
    private var webView: WebView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val url = intent?.getStringExtra(ARG_WEB_URL_TO_OPEN)
        val pageTitle = intent?.getStringExtra(ARG_WEB_PAGE_TITLE)

        load_progress = findViewById(R.id.load_progress_bar)
        card_details.visibility = View.GONE
        webView = findViewById(R.id.webView1)
        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                title = "Loading..."
                if (progress == 100) load_progress_bar.visibility = View.GONE
            }

        }

        webView!!.webViewClient = StoreWebpageOpener.CustomWebViewClient()
        webView!!.settings.javaScriptEnabled = true




        toolbar_backImage.setOnClickListener {
            onBackPressed()
        }

        initWebView(url ?: "", pageTitle ?: "")
    }

    private fun initWebView(url: String, title: String) {


        webView!!.clearHistory()
        webView!!.settings.javaScriptEnabled = true
        webView!!.settings.domStorageEnabled = true
        webView!!.isHorizontalScrollBarEnabled = false
        webView!!.loadUrl(url)
        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                webView!!.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
               // load_progress_bar.clearAnimation()
                load_progress_bar.visibility = View.GONE
            }

        }
    }


}