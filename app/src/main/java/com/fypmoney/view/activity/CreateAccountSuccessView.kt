package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider

import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewLoginSuccessBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.CreateAccountSuccessViewModel
import kotlinx.android.synthetic.main.toolbar.*
import java.util.HashMap

/*
* This class is used for show create account success
* */
class CreateAccountSuccessView :
    BaseActivity<ViewLoginSuccessBinding, CreateAccountSuccessViewModel>() {
    private lateinit var mViewModel: CreateAccountSuccessViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_create_account_success
    }

    override fun getViewModel(): CreateAccountSuccessViewModel {
        mViewModel = ViewModelProvider(this).get(CreateAccountSuccessViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@CreateAccountSuccessView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        setObserver()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onActivateAccountClicked.observe(this) {
            if (Utility.getCustomerDataFromPreference()?.bankProfile?.isAccountActive == AppConstants.NO)
                intentToActivity(AadhaarAccountActivationView::class.java)
            else {
                if (hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
                    intentToActivity(HomeView::class.java)
                } else {
                    intentToActivity(PermissionsActivity::class.java)
                }

            }
        }

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(this@CreateAccountSuccessView, aClass)
        startActivity(intent)
        finishAffinity()
    }


}
