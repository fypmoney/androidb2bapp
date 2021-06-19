package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewBankTransactionHistoryBinding
import com.fypmoney.viewmodel.BankTransactionHistoryViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to handle contacts
* */
class BankTransactionHistoryView :
    BaseActivity<ViewBankTransactionHistoryBinding, BankTransactionHistoryViewModel>(){
    private lateinit var mViewModel: BankTransactionHistoryViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_bank_transaction_history
    }

    override fun getViewModel(): BankTransactionHistoryViewModel {
        mViewModel = ViewModelProvider(this).get(BankTransactionHistoryViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@BankTransactionHistoryView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.trans_history_heading)
        )
        setObserver()
    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {


    }


}
