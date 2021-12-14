package com.fypmoney.view.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityWaitlistBinding
import com.fypmoney.view.activity.NotificationView
import com.fypmoney.view.activity.UserProfileView
import com.fypmoney.view.home.main.homescreen.viewmodel.HomeActivityVM

class WaitlistAprovalActivity : BaseActivity<ActivityWaitlistBinding, HomeActivityVM>() {

    private lateinit var binding: ActivityWaitlistBinding
    private val homeActivityVM by viewModels<HomeActivityVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()

        observeEvents()

    }


    private fun observeEvents() {
        homeActivityVM.event.observe(this, {
            handelEvents(it)
        })
    }

    private fun handelEvents(it: HomeActivityVM.HomeActivityEvent?) {
        when (it) {
            HomeActivityVM.HomeActivityEvent.NotificationClicked -> {
                startActivity(Intent(this, NotificationView::class.java))

            }
            HomeActivityVM.HomeActivityEvent.ProfileClicked -> {
                startActivity(Intent(this, UserProfileView::class.java))
            }

        }
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.activity_home

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): HomeActivityVM = homeActivityVM


}