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
import com.fypmoney.databinding.ViewUserFeedsDetailBinding
import com.fypmoney.databinding.ViewUserFeedsInappBinding
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.FeedDetailsViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds_inapp.*

/*
* This is used to show feed details
* */
class UserFeedsInAppWebview : BaseActivity<ViewUserFeedsInappBinding, FeedDetailsViewModel>() {
    private lateinit var mViewModel: FeedDetailsViewModel
    private lateinit var mViewBinding: ViewUserFeedsInappBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_user_feeds_inapp
    }

    override fun getViewModel(): FeedDetailsViewModel {
        mViewModel = ViewModelProvider(this).get(FeedDetailsViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@UserFeedsInAppWebview,
            toolbar = toolbar1,
            isBackArrowVisible = true
        )



        mViewModel.feedDetails.set(intent.getSerializableExtra(AppConstants.FEED_RESPONSE) as FeedDetails?)
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

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        val mimeType = "text/html"
        val encoding = "UTF-8"


        mViewModel.authorAndDate.set(
            mViewModel.feedDetails.get()?.author + " . " + mViewModel.feedDetails.get()?.readTime + " min read"
        )




                webView.loadDataWithBaseURL(
                    "",
                    mViewModel.feedDetails.get()?.responsiveContent!!,
                    mimeType,
                    encoding,
                    ""
                )


    }


}
