package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewSetPinBinding
import com.fypmoney.databinding.ViewUserFeedsDetailBinding
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.SetPinViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_push_notification.*
import kotlinx.android.synthetic.main.view_push_notification.webView
import kotlinx.android.synthetic.main.view_user_feeds_detail.*

/*
* This is used to show feed details
* */
class SetPinView : BaseActivity<ViewSetPinBinding, SetPinViewModel>() {
    private lateinit var mViewModel: SetPinViewModel
    private lateinit var mViewBinding: ViewSetPinBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_set_pin
    }

    override fun getViewModel(): SetPinViewModel {
        mViewModel = ViewModelProvider(this).get(SetPinViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        webView.settings.javaScriptEnabled = true
        webView.isVerticalScrollBarEnabled = false
        webView.loadUrl(intent.getStringExtra(AppConstants.SET_PIN_URL)!!)
        setObservers()

    }

    fun setObservers()
    {
        mViewModel.onCrossClicked.observe(this)
        {if(it)
        { finish()
            mViewModel.onCrossClicked.value=false
        }
        }
    }


}
