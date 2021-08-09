package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewBankTransactionHistoryBinding
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.FilterByDateFragment
import com.fypmoney.viewmodel.BankTransactionHistoryViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_bank_history.*

/*
* This is used to handle contacts
* */
class BankTransactionHistoryView :
    BaseActivity<ViewBankTransactionHistoryBinding, BankTransactionHistoryViewModel>(),
    FilterByDateFragment.OnFilterByDateClickListener {
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

        toolbarImage.setOnClickListener { callFilterByDateBottomSheet() }
        setObserver()
    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onItemClicked.observe(this) {
            intentToActivity(PayUSuccessView::class.java,it)
        }


    }

    /*
         * This method is used to call filter by date bottom sheet
         * */
    private fun callFilterByDateBottomSheet() {
        val bottomSheet =
            FilterByDateFragment(
                this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "FilterByDate")
    }

    override fun onFilterByDateButtonClick(fromDate: String, toDate: String) {
        mViewModel.callGetBankTransactionHistoryApi(fromDate, toDate)
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(
        aClass: Class<*>,
        bankTransactionHistoryResponseDetails: BankTransactionHistoryResponseDetails
    ) {
        val intent = Intent(this@BankTransactionHistoryView, aClass)
        intent.putExtra(AppConstants.RESPONSE, bankTransactionHistoryResponseDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.BANK_TRANSACTION)
        startActivity(intent)

    }


}
