package com.fypmoney.view.rewardsAndWinnings.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.base.PaginationListener
import com.fypmoney.databinding.FragmentRewardsMoreHistoryBinding
import com.fypmoney.model.HistoryItem
import com.fypmoney.model.RewardHistoryResponseNew
import com.fypmoney.util.AppConstants
import com.fypmoney.view.rewardsAndWinnings.activity.ScratchCardActivity
import com.fypmoney.view.rewardsAndWinnings.adapters.RewardsHistoryBaseAdapter
import com.fypmoney.view.rewardsAndWinnings.interfaces.ListRewardsItemClickListener
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsHistoryVM
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_reward_history.*

class RewardsMoreHistoryFragment :
    BaseFragment<FragmentRewardsMoreHistoryBinding, RewardsHistoryVM>() {

    private val rewardsHistoryVM by viewModels<RewardsHistoryVM> { defaultViewModelProviderFactory }
    private var mViewBinding: FragmentRewardsMoreHistoryBinding? = null
    private var itemsArrayList: ArrayList<RewardHistoryResponseNew> = ArrayList()
    private var isLoading = false
    private var typeAdapterHistory: RewardsHistoryBaseAdapter? = null

    companion object {
        var page = 0
    }

    override fun onTryAgainClicked() {
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_rewards_more_history
    }

    override fun getViewModel(): RewardsHistoryVM = rewardsHistoryVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewBinding = getViewDataBinding()


        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.rewards_history_view)
        )
        setRecyclerView(mViewBinding)
        observeInput()

    }

    private fun observeInput() {

        page = 0
        rewardsHistoryVM.redeemproductDetails.observe(viewLifecycleOwner) {

            Glide.with(this).asDrawable().load(it.scratchResourceHide)
                .into(object : CustomTarget<Drawable?>() {

                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        val intent =
                            Intent(requireContext(), ScratchCardActivity::class.java)
                        intent.putExtra(AppConstants.SECTION_ID, it.sectionId)
                        intent.putExtra(AppConstants.NO_GOLDED_CARD, it.noOfJackpotTicket)
                        it.sectionList?.forEachIndexed { _, item ->
                            if (item != null) {
                                ScratchCardActivity.sectionArrayList.add(item)
                            }
                        }
                        ScratchCardActivity.imageScratch = resource

                        intent.putExtra(
                            AppConstants.ORDER_NUM,
                            rewardsHistoryVM.orderNumber.value
                        )
                        intent.putExtra(
                            AppConstants.PRODUCT_HIDE_IMAGE,
                            it.scratchResourceShow
                        )
                        startActivity(intent)
                    }
                })

        }
        rewardsHistoryVM.rewardHistoryList.observe(
            viewLifecycleOwner
        ) { list ->
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
                list.forEach {
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
        }

    }

    override fun onStart() {
        super.onStart()
        page = 0
        rewardsHistoryVM.callRewardHistory(page)
    }


    private fun setRecyclerView(root: FragmentRewardsMoreHistoryBinding?) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
        val itemClickListener2 = object : ListRewardsItemClickListener {

            override fun onItemClicked(historyItem: HistoryItem) {

                when (historyItem.productType) {
                    AppConstants.PRODUCT_SPIN -> {

                        val productId = historyItem.orderNumber.toString()
                        val productCode = historyItem.productCode.toString()
                        findNavController().navigate(Uri.parse("https://www.fypmoney.in/spinwheel/${productCode}/${productId}"), navOptions {
                            anim {
                                popEnter = R.anim.slide_in_left
                                popExit = R.anim.slide_out_righ
                                enter = R.anim.slide_in_right
                                exit = R.anim.slide_out_left
                            }
                        })
                    }

                    AppConstants.PRODUCT_TREASURE_BOX -> {
                        val productId = historyItem.orderNumber.toString()
                        val productCode = historyItem.productCode.toString()
                        findNavController().navigate(Uri.parse("https://www.fypmoney.in/rotating_treasure/${productCode}/${productId}"), navOptions {
                            anim {
                                popEnter = R.anim.slide_in_left
                                popExit = R.anim.slide_out_righ
                                enter = R.anim.slide_in_right
                                exit = R.anim.slide_out_left
                            }
                        })
                    }
                    AppConstants.PRODUCT_SLOT_MACHINE -> {
                        val productId = historyItem.orderNumber.toString()
                        val productCode = historyItem.productCode.toString()
                        findNavController().navigate(Uri.parse("https://www.fypmoney.in/slot_machine/${productCode}/${productId}"), navOptions {
                            anim {
                                popEnter = R.anim.slide_in_left
                                popExit = R.anim.slide_out_righ
                                enter = R.anim.slide_in_right
                                exit = R.anim.slide_out_left
                            }
                        })
                    }
                    else -> {
                        rewardsHistoryVM.callProductsDetailsApi(historyItem.orderNumber)

                    }
                }
            }

        }

        typeAdapterHistory =
            RewardsHistoryBaseAdapter(itemsArrayList, requireContext(), itemClickListener2)
        root.rvHistory.adapter = typeAdapterHistory
    }

    private fun loadMore(root: FragmentRewardsMoreHistoryBinding?) {
        rewardsHistoryVM.callRewardHistory(page)
        root?.LoadProgressBar?.visibility = View.VISIBLE
        isLoading = true
    }

}