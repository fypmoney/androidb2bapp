package com.fypmoney.view.rewardsAndWinnings.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.PaginationListener
import com.fypmoney.databinding.ViewRewardHistoryBinding
import com.fypmoney.model.HistoryItem

import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.RewardsHistoryLeaderboardAdapter
import com.fypmoney.view.fragment.YourTasksFragment

import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsHistoryVM
import kotlinx.android.synthetic.main.fragment_reward_history.view.*
import kotlinx.android.synthetic.main.fragment_your_task.view.*

import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_reward_history.*


/*
* This is used to handle rewards
* */
class RewardsHistoryView : BaseActivity<ViewRewardHistoryBinding, RewardsHistoryVM>() {
    private var mViewBinding: ViewRewardHistoryBinding? = null
    private lateinit var mVM: RewardsHistoryVM

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    companion object {
        var page = 0

    }

    override fun getLayoutId(): Int {
        return R.layout.view_reward_history
    }

    private var itemsArrayList: ArrayList<HistoryItem> = ArrayList()
    private var isLoading = false
    private var typeAdapterHistory: RewardsHistoryLeaderboardAdapter? = null
    override fun getViewModel(): RewardsHistoryVM {
        mVM = ViewModelProvider(this).get(RewardsHistoryVM::class.java)

        observeInput(mVM!!)
        return mVM
    }

    private fun observeInput(sharedVM: RewardsHistoryVM) {

        page = 0
        sharedVM.rewardHistoryList2.observe(
            this,
            androidx.lifecycle.Observer { list ->
                LoadProgressBar?.visibility = View.GONE
                mViewBinding?.shimmerLayout?.stopShimmer()

                mViewBinding?.shimmerLayout?.visibility = View.GONE
                if (page == 0) {
                    itemsArrayList.clear()

                }
                list.forEach { it ->
                    it.history?.forEach { item ->
                        if (item != null) {
                            itemsArrayList.add(item)
                        }
                    }

                }


                isLoading = false
                typeAdapterHistory?.notifyDataSetChanged()

                if (list.size > 0) {
                    empty_screen?.visibility = View.GONE

                } else {
                    if (page == 0) {
                        empty_screen?.visibility = View.VISIBLE
                    }

                }
                page += 1
            })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()


        setToolbarAndTitle(
            context = this@RewardsHistoryView,
            toolbar = toolbar, backArrowTint = Color.WHITE, titleColor = Color.WHITE,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.rewards_history_view)
        )
        setRecyclerView(mViewBinding)

    }

    override fun onStart() {
        super.onStart()
        page = 0
        mVM.callRewardHistory(page)
    }

    private fun setRecyclerView(root: ViewRewardHistoryBinding?) {
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        root?.rvHistory?.layoutManager = layoutManager

        root?.rvHistory!!.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                loadMore(root)
            }

            override fun loadMoreTopItems() {

            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {

                if (itemsArrayList[pos].productType == AppConstants.PRODUCT_SPIN) {
                    val intent = Intent(this@RewardsHistoryView, SpinWheelViewDark::class.java)
                    SpinWheelViewDark.sectionArrayList.clear()
                    intent.putExtra(
                        AppConstants.ORDER_ID,
                        itemsArrayList[pos].orderNumber.toString()
                    )
                    startActivity(intent)

//                    val args = Bundle()
//                    args.putSerializable("ARRAYLIST", itemsArrayList as Serializable)
//                    intent.putExtra("BUNDLE", args)


                } else {
                    val intent = Intent(this@RewardsHistoryView, ScratchCardActivity::class.java)
                    intent.putExtra(
                        AppConstants.ORDER_ID,
                        itemsArrayList[pos].orderNumber.toString()
                    )
                    startActivity(intent)

                }

            }

            override fun onCallClicked(pos: Int) {


            }


        }

        typeAdapterHistory =
            RewardsHistoryLeaderboardAdapter(itemsArrayList, this, itemClickListener2!!)
        root?.rvHistory?.adapter = typeAdapterHistory
    }

    private fun loadMore(root: ViewRewardHistoryBinding?) {
        mVM?.callRewardHistory(page)
        //LoadProgressBar?.visibility = View.VISIBLE
        root?.LoadProgressBar?.visibility = View.VISIBLE

        isLoading = true

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {


    }


}
