package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewUserFeedsDetailBinding
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.FeedDetailsViewModel
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
            isBackArrowVisible = true
        )

        mViewModel.feedDetails.set(intent.getSerializableExtra(AppConstants.FEED_RESPONSE) as FeedDetails?)
        webView.settings.javaScriptEnabled = true
        webView.isVerticalScrollBarEnabled = false
        val mimeType = "text/html"
        val encoding = "UTF-8"



        when (intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN)) {
            AppConstants.FEED_TYPE_IN_APP_WEBVIEW -> {
                if (mViewModel.feedDetails.get()?.action?.url!!.contains("https://www.youtube.com")) {
                    webView.loadUrl(mViewModel.feedDetails.get()?.action?.url!!)
                    finish()

                } else {
                    webView.loadUrl(mViewModel.feedDetails.get()?.action?.url!!)


                }
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
