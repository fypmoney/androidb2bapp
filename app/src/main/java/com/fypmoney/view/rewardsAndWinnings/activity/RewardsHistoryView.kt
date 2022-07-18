package com.fypmoney.view.rewardsAndWinnings.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.PaginationListener
import com.fypmoney.databinding.ViewRewardHistoryBinding
import com.fypmoney.model.HistoryItem
import com.fypmoney.model.RewardHistoryResponseNew
import com.fypmoney.util.AppConstants
import com.fypmoney.view.arcadegames.ui.SpinWheelHistoryView
import com.fypmoney.view.rewardsAndWinnings.adapters.RewardsHistoryBaseAdapter
import com.fypmoney.view.rewardsAndWinnings.interfaces.ListRewardsItemClickListener
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

    private var itemsArrayList: ArrayList<RewardHistoryResponseNew> = ArrayList()
    private var isLoading = false
    private var typeAdapterHistory: RewardsHistoryBaseAdapter? = null
    override fun getViewModel(): RewardsHistoryVM {
        mVM = ViewModelProvider(this).get(RewardsHistoryVM::class.java)

        return mVM
    }

    private fun observeInput(sharedVM: RewardsHistoryVM?) {

        page = 0
        mVM?.redeemproductDetails.observe(this) {

            Glide.with(this).asDrawable().load(it.scratchResourceHide)
                .into(object : CustomTarget<Drawable?>() {

                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        val intent =
                            Intent(this@RewardsHistoryView, ScratchCardActivity::class.java)
                        intent.putExtra(AppConstants.SECTION_ID, it.sectionId)
                        intent.putExtra(AppConstants.NO_GOLDED_CARD, it.noOfJackpotTicket)
                        it.sectionList?.forEachIndexed { pos, item ->
                            if (item != null) {
                                ScratchCardActivity.sectionArrayList.add(item)
                            }
                        }
                        ScratchCardActivity.imageScratch = resource

                        intent.putExtra(
                            AppConstants.ORDER_NUM,
                            mVM.orderNumber.value
                        )
                        intent.putExtra(
                            AppConstants.PRODUCT_HIDE_IMAGE,
                            it.scratchResourceShow
                        )
                        startActivity(intent)
                    }
                })

        }
        sharedVM?.rewardHistoryList?.observe(
            this,
            { list ->
                LoadProgressBar?.visibility = View.GONE
                mViewBinding?.shimmerLayout?.stopShimmer()

                mViewBinding?.shimmerLayout?.visibility = View.GONE
                if (page == 0) {
                    itemsArrayList.clear()
                }
                if (itemsArrayList.size > 0 && list.isNotEmpty() && itemsArrayList[itemsArrayList.size - 1].date == list[0].date) {
                    list.forEachIndexed { pos, item ->

                        if (pos == 0) {
                            list[0].history?.let {
                                itemsArrayList[itemsArrayList.size - 1].history?.addAll(
                                    it
                                )
                            }
                        } else {
                            itemsArrayList.add(item)
                        }
                    }

                } else {
                    list.forEach { it ->
                        itemsArrayList.add(it)
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
        observeInput(mVM)
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
        var itemClickListener2 = object : ListRewardsItemClickListener {


            override fun onItemClicked(historyItem: HistoryItem) {

                when (historyItem.productType) {
                    AppConstants.PRODUCT_SPIN -> {
                        val intent = Intent(this@RewardsHistoryView, SpinWheelHistoryView::class.java)
                        SpinWheelViewDark.sectionArrayList.clear()
                        intent.putExtra(AppConstants.NO_GOLDED_CARD, historyItem.noOfJackpotTicket)

                        intent.putExtra(
                            AppConstants.ORDER_NUM,
                            historyItem.orderNumber.toString()
                        )
                        startActivity(intent)
                    }

                    AppConstants.PRODUCT_TREASURE_BOX -> {
//                        findNavController().navigate(R.id.navigation_rotating_treasure)
                    }
                    else -> {
                        mVM.callProductsDetailsApi(historyItem.orderNumber)

                    }
                }
//                if (historyItem.productType == AppConstants.PRODUCT_SPIN) {
//                    val intent = Intent(this@RewardsHistoryView, SpinWheelViewDark::class.java)
//                    SpinWheelViewDark.sectionArrayList.clear()
//                    intent.putExtra(AppConstants.NO_GOLDED_CARD, historyItem.noOfJackpotTicket)
//
//                    intent.putExtra(
//                        AppConstants.ORDER_NUM,
//                        historyItem.orderNumber.toString()
//                    )
//                    startActivity(intent)
//
//
//                } else if (historyItem.productType == AppConstants.PRODUCT_TREASURE_BOX) {
//                    findNavController().navigate(R.id.navigation_rotating_treasure)
//                } else {
//
//                    mVM.callProductsDetailsApi(historyItem.orderNumber)
//
//                }
            }


        }

        typeAdapterHistory =
            RewardsHistoryBaseAdapter(itemsArrayList, this, itemClickListener2!!)
        root?.rvHistory?.adapter = typeAdapterHistory
    }

    private fun loadMore(root: ViewRewardHistoryBinding?) {
        mVM?.callRewardHistory(page)

        root?.LoadProgressBar?.visibility = View.VISIBLE

        isLoading = true

    }


}
