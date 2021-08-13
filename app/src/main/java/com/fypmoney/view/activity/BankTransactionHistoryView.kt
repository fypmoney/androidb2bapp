package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.PaginationListener
import com.fypmoney.databinding.ViewBankTransactionHistoryBinding
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.FilterByDateFragment
import com.fypmoney.viewmodel.BankTransactionHistoryViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_bank_history.*
import kotlinx.android.synthetic.main.view_bank_transaction_history.*


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

    var isLoading: Boolean = false
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
        setRecylerView()
    }

    var page = 1
    private fun setRecylerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recycler_view.layoutManager = layoutManager

        recycler_view.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                Log.d("chackpaginat", "dc")
                loadMore()
            }

            override fun loadMoreTopItems() {


                Log.d("chackpaginat", "upar")
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
        mViewModel!!.LoadedList.observe(this, androidx.lifecycle.Observer { list ->
            if (list != null) {


                LoadProgressBar?.visibility = View.GONE

                if (list.isNotEmpty()) {
                    var arraylist = list

                    mViewModel.bankTransactionHistoryAdapter.setList(arraylist)
                } else {
                    if (page == 0) {
                        mViewModel.noDataFoundVisibility.set(true)
                    }
                }
                page = page + 1
                isLoading = false

            } else {
                if (page == 0) {
                    mViewModel.noDataFoundVisibility.set(true)
                }
            }

        })
    }

    private fun loadMore() {

        LoadProgressBar?.visibility = View.VISIBLE

        isLoading = true
        mViewModel.callGetBankTransactionHistoryApi(page = page)


    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onItemClicked.observe(this) {
            intentToActivity(PayUSuccessView::class.java, it)
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
