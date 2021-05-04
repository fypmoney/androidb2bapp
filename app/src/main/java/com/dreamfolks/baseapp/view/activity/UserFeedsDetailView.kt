package com.dreamfolks.baseapp.view.activity

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.dreamfolks.baseapp.BR
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.base.BaseActivity
import com.dreamfolks.baseapp.databinding.ViewUserFeedsDetailBinding
import com.dreamfolks.baseapp.model.FeedDetails
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.viewmodel.FeedDetailsViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_push_notification.*
import kotlinx.android.synthetic.main.view_push_notification.webView
import kotlinx.android.synthetic.main.view_user_feeds_detail.*

/*
* This is used to show feed details
* */
class UserFeedsDetailView : BaseActivity<ViewUserFeedsDetailBinding, FeedDetailsViewModel>() {
    private lateinit var mViewModel: FeedDetailsViewModel
    private lateinit var mViewBinding: ViewUserFeedsDetailBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_user_feeds_detail
    }

    override fun getViewModel(): FeedDetailsViewModel {
        mViewModel = ViewModelProvider(this).get(FeedDetailsViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@UserFeedsDetailView,
            toolbar = toolbar,
            textView = toolbar_title,
            toolbarTitle = getString(R.string.feeds_details_screen_title),
            isBackArrowVisible = true
        )

        mViewModel.feedDetails.set(intent.getSerializableExtra(AppConstants.FEED_RESPONSE) as FeedDetails?)
        webView.settings.javaScriptEnabled = true
        webView.isVerticalScrollBarEnabled = false
        val mimeType = "text/html"
        val encoding = "UTF-8"

        when (intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN)) {
            AppConstants.FEED_TYPE_IN_APP_WEBVIEW -> {
                webView.loadUrl(mViewModel.feedDetails.get()?.action?.url!!)
            }
            else -> {
                webView.loadDataWithBaseURL(
                    "",
                    mViewModel.feedDetails.get()?.responsiveContent!!,
                    mimeType,
                    encoding,
                    ""
                )
            }

        }


        setObserver()
    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {


    }

}
