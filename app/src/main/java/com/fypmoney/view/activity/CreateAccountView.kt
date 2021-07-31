package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.R
import com.fypmoney.BR
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewCreateAccountBinding
import com.fypmoney.databinding.ViewSelectInterestBinding
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.CreateAccountViewModel
import kotlinx.android.synthetic.main.screen_home.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.view_create_account.*

/*
* This is used to handle create account functionality
* */
class CreateAccountView :
    BaseActivity<ViewCreateAccountBinding, CreateAccountViewModel>(), Utility.OnDateSelected {
    private lateinit var mViewModel: CreateAccountViewModel
    private lateinit var mViewBinding: ViewCreateAccountBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_create_account
    }

    override fun getViewModel(): CreateAccountViewModel {
        mViewModel = ViewModelProvider(this).get(CreateAccountViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@CreateAccountView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        btnCreateAccount.backgroundTintList =
            ContextCompat.getColorStateList(applicationContext, R.color.buttonUnselectedColor)

        try {
            mViewModel.setData(intent.getSerializableExtra((AppConstants.CUSTOMER_INFO_RESPONSE)) as CustomerInfoResponseDetails)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        setObserver()
    }

/*
Create this method for observe the viewModel fields
 */

    private fun setObserver() {
        mViewModel.onDobClicked.observe(this) {
            if (it) {
                try {
                    Utility.showDatePickerDialog(context = this, this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mViewModel.onDobClicked.value = false
            }
        }
        mViewModel.isEnabled.observe(this) {
                    if (it) {
                        btnCreateAccount.backgroundTintList =
                            ContextCompat.getColorStateList(applicationContext, R.color.text_color_dark)

                        mViewModel.isEnabled.value = false
            }
        }
        mViewModel.onUpdateProfileSuccess.observe(this) {
            if (it) {
                intentToActivity(CreateAccountSuccessView::class.java)
                mViewModel.onUpdateProfileSuccess.value = false
            }
        }
        mViewModel.firstName.observe(this) {
            if (!TextUtils.isEmpty(mViewModel.firstName.value)) {
                if (!TextUtils.isEmpty(mViewModel.lastName.value)) {
                    if(!TextUtils.isEmpty(mViewModel.dob.value)){
                        mViewModel.isEnabled.value=true
                    }
                }

            }
            else{
                btnCreateAccount.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.buttonUnselectedColor)

            }
        }
        mViewModel.lastName.observe(this) {
            if (!TextUtils.isEmpty(mViewModel.lastName.value)) {
                if (!TextUtils.isEmpty(mViewModel.firstName.value)) {
                    if(!TextUtils.isEmpty(mViewModel.dob.value)){
                        mViewModel.isEnabled.value=true
                    }
                }

            }
            else{
                btnCreateAccount.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.buttonUnselectedColor)

            }
        }

        mViewModel.dob.observe(this) {
            if (!TextUtils.isEmpty(mViewModel.dob.value)) {
                if (!TextUtils.isEmpty(mViewModel.firstName.value)) {
                    if(!TextUtils.isEmpty(mViewModel.lastName.value)){
                        mViewModel.isEnabled.value=true
                    }
                }

            }
            else{
                btnCreateAccount.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.buttonUnselectedColor)

            }
        }



        mViewModel.onLoginClicked.observe(this) {
            if (it) {
                intentToActivity(FirstScreenView::class.java)
                mViewModel.onLoginClicked.value = false
            }
        }

    }

    override fun onDateSelected(dateOnEditText: String, dateOnServer: String, yearDifference: Int) {
        mViewModel.dob.value = dateOnEditText
        mViewModel.dobForServer.value = dateOnServer
    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@CreateAccountView, aClass))
        finish()
    }


}
