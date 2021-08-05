package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewSelectInterestBinding
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.SelectInterestViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar

/*
* This is used to handle interest of the user
* */
class SelectInterestView :
    BaseActivity<ViewSelectInterestBinding, SelectInterestViewModel>() {
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
            toolbar = toolbar, isBackArrowVisible = true
        )



        setObserver()
    }

/*
Create this method for observe the viewModel fields
 */

    private fun setObserver() {
        mViewModel.onUpdateProfileSuccess.observe(this) {
            if (it) {
                Utility.showToast("Your interest has been updated successfully")
                mViewModel.onUpdateProfileSuccess.value = false
                finish()
            }
        }
        mViewModel.onAnswerLater.observe(this) {
            if (it) {
                mViewModel.onAnswerLater.value = false
                finish()
            }
        }

    }


}
