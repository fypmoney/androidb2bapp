package com.fypmoney.view.rewardsAndWinnings


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.PaginationListener
import com.fypmoney.databinding.CashbackWonActivityBinding
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.insights.model.AllTxnItem
import com.fypmoney.view.insights.view.CategoryWaiseTransactionDetailsFragment
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsCashbackwonVM
import kotlinx.android.synthetic.main.cashback_won_activity.*
import kotlinx.android.synthetic.main.toolbar.*


class CashBackWonHistoryActivity :
    BaseActivity<CashbackWonActivityBinding, RewardsCashbackwonVM>() {
    var isLoading: Boolean = false
    var page = 0
    private var mViewBinding: Int? = null
    var mViewModel: RewardsCashbackwonVM? = null


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
        setRecylerView()


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
        mViewModel.rewardHistoryList.observe(this) { list ->
            if (list != null) {
                LoadProgressBar?.visibility = View.GONE


                if (list.isNotEmpty()) {
                    val arraylist = list
                    page += 1

                    mViewModel.noDataFoundVisibility.set(false)
                    mViewModel.cashbackHistoryAdapter.setList(arraylist)
                } else {
                    if (page == 0) {
                        mViewModel.noDataFoundVisibility.set(true)
                    }
                }

                isLoading = false

            } else {

                if (page == 0) {

                    mViewModel.noDataFoundVisibility.set(true)
                }
            }

        }
        mViewModel.onItemClicked.observe(this) {

            val allTxnItemModel = AllTxnItem(
                amount = it.amount,
                paymentMode = it.paymentMode,
                accReferenceNumber = it.accReferenceNumber,
                mrn = it.mrn,
                mobileNo = it.mobileNo,
                categoryCode = it.categoryCode,
                message = it.message,
                userName = it.userName,
                transactionDate = it.transactionDate,
                transactionType = it.transactionType,
                bankReferenceNumber = it.bankReferenceNumber,
                iconLink = it.iconLink,
                category = it.category
            )
            addFragmentToActivity(allTxnItemModel)
        }
    }

    private fun intentToActivity(
        aClass: Class<*>,
        transactionDetails: BankTransactionHistoryResponseDetails
    ) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.RESPONSE, transactionDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.BANK_TRANSACTION)
        startActivity(intent)

    }

    private fun loadMore() {

        LoadProgressBar?.visibility = View.VISIBLE
        isLoading = true
        mViewModel?.callRewardHistory(page)


    }

    private fun addFragmentToActivity(allTxnItem: AllTxnItem){
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_righ);
        val fragment = CategoryWaiseTransactionDetailsFragment()
        fragment.arguments = bundleOf(Pair("txnDetail",allTxnItem))
        tr.add(R.id.container, fragment)
        tr.addToBackStack("TransactionHistoryDetails")
        tr.commit()
    }

}
