package com.fypmoney.view.rewardsAndWinnings

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
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsCashbackwonVM
import kotlinx.android.synthetic.main.view_bank_transaction_history.*


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
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )


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

        recycler_view.layoutManager = layoutManager

        recycler_view.addOnScrollListener(object : PaginationListener(layoutManager) {
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
        mViewModel!!.rewardHistoryList.observe(this, androidx.lifecycle.Observer { list ->
            if (list != null) {


                LoadProgressBar?.visibility = View.GONE


                if (list.isNotEmpty()) {
                    var arraylist = list
                    page = page + 1

                    mViewModel.noDataFoundVisibility.set(false)
                    mViewModel.bankTransactionHistoryAdapter.setList(arraylist)
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

    }

    private fun loadMore() {

        LoadProgressBar?.visibility = View.VISIBLE
        isLoading = true
        mViewModel?.callRewardHistory(page)


    }

}
