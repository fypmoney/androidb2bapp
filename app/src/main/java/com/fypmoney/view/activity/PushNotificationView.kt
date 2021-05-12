package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewPushNotificationBinding
import com.fypmoney.viewmodel.PushNotificationViewModel


/*
* This class is used for handling push notifications
* */
class PushNotificationView : BaseActivity<ViewPushNotificationBinding, PushNotificationViewModel>() {
    private lateinit var mViewModel: PushNotificationViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_push_notification
    }

    override fun getViewModel(): PushNotificationViewModel {
        mViewModel = ViewModelProvider(this).get(PushNotificationViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {


    }


}
