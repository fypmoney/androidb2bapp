package com.fypmoney.viewmodel

import android.app.Application
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.Utility

/*
* This is used to handle push notification
* */
class PushNotificationViewModel(application: Application) : BaseViewModel(application) {
    var progressBarVisibility = ObservableField(true)
    var url = ObservableField("https://www.google.com/")


    class MyWebViewClient internal constructor(
        private val progressBarVisibility: ObservableField<Boolean>
    ) : WebViewClient() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url: String = request?.url.toString()
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }


        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            progressBarVisibility.set(false)
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            progressBarVisibility.set(false)
            Utility.showToast("Unable to load, Please try again")
        }
    }

    fun getWebViewClient(): WebViewClient {
        return MyWebViewClient(progressBarVisibility)
    }


}