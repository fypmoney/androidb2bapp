package com.dreamfolks.baseapp.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.BR
import com.dreamfolks.baseapp.base.BaseActivity
import com.dreamfolks.baseapp.databinding.ViewSelectInterestBinding
import com.dreamfolks.baseapp.model.CustomerInfoResponse
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.util.Utility
import com.dreamfolks.baseapp.viewmodel.SelectInterestViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar

/*
* This is used to handle interest of the user
* */
class SelectInterestView :
    BaseActivity<ViewSelectInterestBinding, SelectInterestViewModel>(), Utility.OnDateSelected {
    private lateinit var mViewModel: SelectInterestViewModel
    private lateinit var mViewBinding: ViewSelectInterestBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_select_interest
    }

    override fun getViewModel(): SelectInterestViewModel {
        mViewModel = ViewModelProvider(this).get(SelectInterestViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@SelectInterestView,
            toolbar = toolbar,
            textView = toolbar_title,
            toolbarTitle = getString(R.string.fill_details)
        )
        try {
            mViewModel.setData(intent.getSerializableExtra((AppConstants.CUSTOMER_INFO_RESPONSE)) as CustomerInfoResponse)
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
        mViewModel.onUpdateProfileSuccess.observe(this) {
            if (it) {
                intentToActivity(HomeView::class.java)
                mViewModel.onUpdateProfileSuccess.value = false
            }
        }

    }

    override fun onDateSelected(dateOnEditText: String, dateOnServer: String, yearDifference: Int) {
        mViewModel.dob.value = dateOnEditText
        mViewModel.dobForServer.value = dateOnServer
        mViewModel.isMajorMinorVisible.set(true)
        when {
            yearDifference < 18 -> {
                mViewModel.majorMinorText.set(getString(R.string.minor_text))
            }
            else -> {
                mViewModel.majorMinorText.set(getString(R.string.major_text))
            }
        }
    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@SelectInterestView, aClass))
        finish()
    }


}
