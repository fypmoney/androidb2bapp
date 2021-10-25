package com.fypmoney.view.rewardsAndWinnings.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewSpinHistoryBinding

import com.fypmoney.view.rewardsAndWinnings.viewModel.SpinHistoryViewModel

import kotlinx.android.synthetic.main.toolbar.*


/*
* This is used to handle rewards
* */
class SpinHistoryView : BaseActivity<ViewSpinHistoryBinding, SpinHistoryViewModel>() {
    private lateinit var mViewModel: SpinHistoryViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_spin_history
    }

    override fun getViewModel(): SpinHistoryViewModel {
        mViewModel = ViewModelProvider(this).get(SpinHistoryViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@SpinHistoryView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.rewards_history_view)
        )
        setObserver()

    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {


    }


}
