package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.PaginationListener
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewTransactionHistoryBinding
import com.fypmoney.model.TransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.TransactionHistoryViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_first_screen.*
import kotlinx.android.synthetic.main.view_transaction_history.*


/*
* This class is used to handle transaction history of a particular contact
* */
class TransactionHistoryView :
    BaseActivity<ViewTransactionHistoryBinding, TransactionHistoryViewModel>() {
    private lateinit var mViewModel: TransactionHistoryViewModel


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_transaction_history
    }

    var isLoading: Boolean = false

    override fun getViewModel(): TransactionHistoryViewModel {
        mViewModel = ViewModelProvider(this).get(TransactionHistoryViewModel::class.java)
        return mViewModel
    }

    var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.setResponseAfterContactSelected(intent.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE))
        mViewModel.callGetTransactionHistoryApi(0)

        setToolbarAndTitle(
            context = this@TransactionHistoryView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = mViewModel.contactName.get()
        )
        setObserver()
        setRecylerView()


    }

    private fun setRecylerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        recycler_view.layoutManager = layoutManager

        recycler_view.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                Log.d("chackpaginat", "dc")

            }

            override fun loadMoreTopItems() {
                loadMore()
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

                    mViewModel.transactionHistoryAdapter.setList(arraylist)
                }
                page = page + 1
                isLoading = false

            } else {
                if (page == 0) {
                    mViewModel.isNoDataFoundVisible.set(true)
                }
            }

        })
    }

    private fun loadMore() {

        LoadProgressBar?.visibility = View.VISIBLE

        isLoading = true
        mViewModel.callGetTransactionHistoryApi(page)


    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

        mViewModel.onItemClicked.observe(this) {

        intentToPayUActivity(PayUSuccessView::class.java, it)
        }

        mViewModel.onPayOrRequestClicked.observe(this) {
            when (it.id) {
                R.id.pay -> {
                    intentToActivity(
                        contactEntity = mViewModel.contactResult.get(),
                        aClass = EnterAmountForPayRequestView::class.java, AppConstants.PAY
                    )
                }
                R.id.request -> {
                    intentToActivity(
                        contactEntity = mViewModel.contactResult.get(),
                        aClass = EnterAmountForPayRequestView::class.java, AppConstants.REQUEST
                    )
                }

            }
        }

    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(contactEntity: ContactEntity?, aClass: Class<*>, action: String) {
        val intent = Intent(this@TransactionHistoryView, aClass)
        intent.putExtra(AppConstants.CONTACT_SELECTED_RESPONSE, contactEntity)
        intent.putExtra(AppConstants.WHICH_ACTION, action)
        startActivity(intent)
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToPayUActivity(
        aClass: Class<*>,
        transactionHistoryResponseDetails: TransactionHistoryResponseDetails
    ) {
        Log.d("chacktrans", transactionHistoryResponseDetails.toString())
        val intent = Intent(this@TransactionHistoryView, aClass)
        intent.putExtra(AppConstants.RESPONSE, transactionHistoryResponseDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.TRANSACTION)
        startActivity(intent)

    }

}
