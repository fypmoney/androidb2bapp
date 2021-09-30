package com.fypmoney.view.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.HtmlWebOpenerBinding
import com.fypmoney.databinding.ViewUserFeedsDetailBinding
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.FeedDetailsViewModel
import kotlinx.android.synthetic.main.html_web_opener.*


/*
* This is used to show feed details
* */
class HtmlOpenerView : BaseActivity<HtmlWebOpenerBinding, FeedDetailsViewModel>() {
    private lateinit var mViewModel: FeedDetailsViewModel
    private lateinit var mViewBinding: HtmlWebOpenerBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    companion object {
        var tandC: String = ""
    }

    override fun getLayoutId(): Int {
        return R.layout.html_web_opener
    }

    override fun getViewModel(): FeedDetailsViewModel {
        mViewModel = ViewModelProvider(this).get(FeedDetailsViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()





        webView.settings.apply {
            javaScriptEnabled = true
        }
        webView.isVerticalScrollBarEnabled = false

        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                webView!!.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

            }

        }

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        val mimeType = "text/html"
        val encoding = "UTF-8"

        //  webView.loadUrl("https://www.google.com/")


        webView.loadDataWithBaseURL(
            "",
            tandC,
            mimeType,
            encoding,
            ""
        )


    }


}
