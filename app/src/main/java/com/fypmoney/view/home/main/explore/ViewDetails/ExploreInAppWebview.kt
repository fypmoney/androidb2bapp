package com.fypmoney.view.home.main.explore.ViewDetails

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewExploreInappBinding
import com.fypmoney.databinding.ViewUserFeedsDetailBinding
import com.fypmoney.databinding.ViewUserFeedsInappBinding
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.home.main.explore.viewmodel.ExploreDetailsViewModel
import com.fypmoney.viewmodel.FeedDetailsViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds_inapp.*

/*
* This is used to show feed details
* */
class ExploreInAppWebview : BaseActivity<ViewExploreInappBinding, ExploreDetailsViewModel>() {
    private lateinit var mViewModel: ExploreDetailsViewModel
    private lateinit var mViewBinding: ViewExploreInappBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_explore_inapp
    }

    override fun getViewModel(): ExploreDetailsViewModel {
        mViewModel = ViewModelProvider(this).get(ExploreDetailsViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@ExploreInAppWebview,
            toolbar = toolbar1,
            isBackArrowVisible = true
        )


        var url = intent.getStringExtra(AppConstants.IN_APP_URL)



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
                load_progress_bar.visibility = View.GONE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                load_progress_bar.visibility = View.VISIBLE
            }

        }

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)


        if (url != null) {
            if (url.contains("https://www.youtube.com")) {
                webView.loadUrl(url)
                finish()

            } else {
                webView.loadUrl(url)

            }
        }

        val mimeType = "text/html"
        val encoding = "UTF-8"


    }



}
