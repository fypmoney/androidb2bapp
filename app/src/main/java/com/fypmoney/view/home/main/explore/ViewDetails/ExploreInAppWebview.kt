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
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.home.main.explore.viewmodel.ExploreDetailsViewModel
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


        val url = intent.getStringExtra(AppConstants.IN_APP_URL)




        mViewBinding.webView.setMixedContentAllowed(false)

        mViewBinding.webView.setCookiesEnabled(true)
        mViewBinding.webView.settings.setSupportMultipleWindows(true)
        mViewBinding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        mViewBinding.webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("https://www.youtube.com")) {
                    mViewBinding.webView.loadUrl(url)
                    finish()

                } else {
                    mViewBinding.webView.loadUrl(url)
                }
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
        if (url != null) {
            mViewBinding.webView.loadUrl(url)
        }else{
            Utility.showToast(getString(R.string.unable_to_open_page_please_try_again_later))
        }
    }



}
