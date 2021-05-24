package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewActivationSuccessWithAadhaarBinding
import com.fypmoney.databinding.ViewInviteRejectedBinding
import com.fypmoney.databinding.ViewStayTunedBinding
import com.fypmoney.viewmodel.InviteMemberViewModel
import com.fypmoney.viewmodel.ActivationSuccessWithAadhaarViewModel
import kotlinx.android.synthetic.main.toolbar.*

/*
* This class is used to show success message on account activation with Aadhaar
* */
class ActivationSuccessWithAadhaarView : BaseActivity<ViewActivationSuccessWithAadhaarBinding, ActivationSuccessWithAadhaarViewModel>() {
    private lateinit var mViewModel: ActivationSuccessWithAadhaarViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_activation_success_with_aadhaar
    }

    override fun getViewModel(): ActivationSuccessWithAadhaarViewModel {
        mViewModel = ViewModelProvider(this).get(ActivationSuccessWithAadhaarViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@ActivationSuccessWithAadhaarView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        setObserver()

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onContinueClicked.observe(this)
        {
            if (it) {
                intentToActivity(HomeView::class.java)
                mViewModel.onContinueClicked.value = false
            }

        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@ActivationSuccessWithAadhaarView, aClass))
        finishAffinity()
    }

}