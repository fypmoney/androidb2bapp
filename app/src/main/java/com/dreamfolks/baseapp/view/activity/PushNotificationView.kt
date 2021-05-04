package com.dreamfolks.baseapp.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.dreamfolks.baseapp.BR
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.base.BaseActivity
import com.dreamfolks.baseapp.databinding.ViewPushNotificationBinding
import com.dreamfolks.baseapp.viewmodel.PushNotificationViewModel


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
