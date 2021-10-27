package com.fypmoney.view.rewardsAndWinnings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR

import com.fypmoney.base.BaseActivity


import kotlinx.android.synthetic.main.toolbar.*
import com.fypmoney.R
import com.fypmoney.base.PaginationListener
import com.fypmoney.databinding.CashbackWonActivityBinding
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.activity.PayUSuccessView
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsCashbackwonVM
import kotlinx.android.synthetic.main.cashback_won_activity.*


class CashBackWonHistoryActivity :
    BaseActivity<CashbackWonActivityBinding, RewardsCashbackwonVM>() {
    var isLoading: Boolean = false
    var page = 1
    private var mViewBinding: Int? = null
    var mViewModel: RewardsCashbackwonVM? = null

    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mViewBinding = getBindingVariable()
        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.cash_back_won_history),
            titleColor = Color.BLACK,
            backArrowTint = Color.BLACK
        )
        setRecylerView()


    }


    override fun onStart() {
        super.onStart()


    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.cashback_won_activity
    }

    override fun getViewModel(): RewardsCashbackwonVM {
        mViewModel = ViewModelProvider(this).get(RewardsCashbackwonVM::class.java)
        setObserver(mViewModel!!)

        return mViewModel!!
    }

    private fun setRecylerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rv_cashback.layoutManager = layoutManager

        rv_cashback.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {

                loadMore()
            }

            override fun loadMoreTopItems() {


            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

    }
    private fun setObserver(mViewModel: RewardsCashbackwonVM) {
        mViewModel?.rewardHistoryList.observe(this, androidx.lifecycle.Observer { list ->
            if (list != null) {


                LoadProgressBar?.visibility = View.GONE


                if (list.isNotEmpty()) {
                    var arraylist = list
                    page = page + 1

                    mViewModel.noDataFoundVisibility.set(false)
                    mViewModel.cashbackHistoryAdapter.setList(arraylist)
                } else {

                    if (page == 1) {
                        mViewModel.noDataFoundVisibility.set(true)
                    }
                }

                isLoading = false

            } else {

                if (page == 1) {

                    mViewModel.noDataFoundVisibility.set(true)
                }
            }

        })
        mViewModel.onItemClicked.observe(this) {
            intentToActivity(PayUSuccessView::class.java, it)
        }
    }

    private fun intentToActivity(
        aClass: Class<*>,
        bankTransactionHistoryResponseDetails: BankTransactionHistoryResponseDetails
    ) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.RESPONSE, bankTransactionHistoryResponseDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.BANK_TRANSACTION)
        startActivity(intent)

    }

    private fun loadMore() {

        LoadProgressBar?.visibility = View.VISIBLE
        isLoading = true
        mViewModel?.callRewardHistory(page)


    }

}
