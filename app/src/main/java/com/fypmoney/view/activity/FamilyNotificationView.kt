package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewFamilyNotificationBinding
import com.fypmoney.databinding.ViewUserFeedsBinding
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.FEED_RESPONSE
import com.fypmoney.viewmodel.FamilyNotificationViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to show list of family notification
* */
class FamilyNotificationView : BaseActivity<ViewFamilyNotificationBinding, FamilyNotificationViewModel>(){
    private lateinit var mViewModel: FamilyNotificationViewModel
    private lateinit var mViewBinding: ViewFamilyNotificationBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_family_notification
    }

    override fun getViewModel(): FamilyNotificationViewModel {
        mViewModel = ViewModelProvider(this).get(FamilyNotificationViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@FamilyNotificationView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        setObserver()
    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {


    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>, feedDetails: FeedDetails, type: String? = null) {
        val intent = Intent(this@FamilyNotificationView, aClass)
        intent.putExtra(FEED_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponse())
        startActivity(intent)
    }

}
