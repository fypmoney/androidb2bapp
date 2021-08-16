package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewRewardsHistoryBinding
import com.fypmoney.viewmodel.RewardsHistoryViewModel

import kotlinx.android.synthetic.main.toolbar.*


/*
* This is used to handle rewards
* */
class RewardsHistoryView : BaseActivity<ViewRewardsHistoryBinding, RewardsHistoryViewModel>() {
    private lateinit var mViewModel: RewardsHistoryViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_rewards_history
    }

    override fun getViewModel(): RewardsHistoryViewModel {
        mViewModel = ViewModelProvider(this).get(RewardsHistoryViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@RewardsHistoryView,
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
