package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAccountActivationSuccessBinding
import com.fypmoney.databinding.ViewStayTunedBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.AccountActivationSuccessViewModel
import com.fypmoney.viewmodel.StayTunedViewModel
import kotlinx.android.synthetic.main.toolbar.*

/*
* This class is used to display successful account activation
* */
class AccountActivationSuccessView : BaseActivity<ViewAccountActivationSuccessBinding, AccountActivationSuccessViewModel>() {
    private lateinit var mViewModel: AccountActivationSuccessViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_account_activation_success
    }

    override fun getViewModel(): AccountActivationSuccessViewModel {
        mViewModel = ViewModelProvider(this).get(AccountActivationSuccessViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@AccountActivationSuccessView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
    }

}
