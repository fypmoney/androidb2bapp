package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewLoginSuccessBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.LoginSuccessViewModel

/*
* This class is used for show login success message
* */
class LoginSuccessView : BaseActivity<ViewLoginSuccessBinding, LoginSuccessViewModel>() {
    private lateinit var mViewModel: LoginSuccessViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_login_success
    }

    override fun getViewModel(): LoginSuccessViewModel {
        mViewModel = ViewModelProvider(this).get(LoginSuccessViewModel::class.java)
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
        mViewModel.onApiSuccess.observe(this) {
            when {
                Utility.getCustomerDataFromPreference()?.isReferralAllowed == AppConstants.YES -> {
                    intentToActivity(ReferralCodeView::class.java)
                }
                Utility.getCustomerDataFromPreference()?.isProfileCompleted == AppConstants.NO -> {
                    intentToActivity(CreateAccountView::class.java)
                }
                else->{
                    intentToActivity(HomeView::class.java)
                }


            }


        }

    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(this@LoginSuccessView, aClass)
        intent.putExtra(
            AppConstants.IS_PROFILE_COMPLETED,
            Utility.getCustomerDataFromPreference()?.isProfileCompleted
        )
        startActivity(intent)
    }
}
