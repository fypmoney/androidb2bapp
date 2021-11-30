package com.fypmoney.view.rewardsAndWinnings

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR

import com.fypmoney.base.BaseActivity

import com.fypmoney.view.fragment.*

import com.google.android.material.tabs.TabLayout

import java.util.ArrayList


import com.fypmoney.databinding.ViewRewardsBinding
import kotlinx.android.synthetic.main.toolbar.*

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.R
import com.fypmoney.util.AppConstants
import com.fypmoney.view.activity.HomeView
import com.fypmoney.view.rewardsAndWinnings.activity.ScratchCardActivity
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardHistoryFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsJackpotFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsOverviewFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsSpinnerListFragment
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*


class RewardsActivity : BaseActivity<ViewRewardsBinding, RewardsAndVM>() {


    private var dialogInsufficientFund: Dialog? = null
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    var mViewModel: RewardsAndVM? = null

    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialogInsufficientFund = Dialog(this)

        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.quick_actions_rewards),
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        initializeTabs(tabLayout, intent)



        mViewModel?.totalmyntsClicked?.observe(
            this,
            androidx.lifecycle.Observer { list ->
                if (list) {
                    viewPager.currentItem = 3
                    mViewModel?.totalmyntsClicked?.postValue(false)

                }
            }
        )
        mViewModel?.error?.observe(
            this,
            androidx.lifecycle.Observer { list ->

                if (list.errorCode == "PKT_2051") {

                    callInsuficientFundDialog(list.msg)
                }

            }
        )


    }

    private fun callInsuficientFundDialog(msg: String) {
        dialogInsufficientFund?.setCancelable(false)
        dialogInsufficientFund?.setCanceledOnTouchOutside(false)
        dialogInsufficientFund?.setContentView(R.layout.dialog_rewards_insufficient)

        val wlp = dialogInsufficientFund?.window?.attributes

        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialogInsufficientFund?.setCanceledOnTouchOutside(false)




        dialogInsufficientFund?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogInsufficientFund?.window?.attributes = wlp
        dialogInsufficientFund?.error_msg?.text = msg



        dialogInsufficientFund?.clicked?.setOnClickListener(View.OnClickListener {


            trackr {

                it.name = TrackrEvent.insufficient_mynts

            }
            dialogInsufficientFund?.dismiss()
        })


        dialogInsufficientFund?.show()
    }

    private fun callActivity(aClass: Class<*>) {
        val intent = Intent(this, aClass)


        startActivity(intent)
    }

    internal class ViewPagerAdapter(manager: FragmentManager?) :
        FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }


    override fun onStart() {
        super.onStart()
        mViewModel?.callTotalRewardsEarnings()
        mViewModel?.callRewardSummary()
        mViewModel?.callRewardHistory()


    }

    private fun initializeTabs(tabLayout: TabLayout, intent: Intent) {


        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(RewardsOverviewFragment(), getString(R.string.overview))

        adapter.addFragment(RewardsSpinnerListFragment(), getString(R.string.arcade))
        adapter.addFragment(RewardsJackpotFragment(), getString(R.string.jackpot))
        adapter.addFragment(RewardHistoryFragment(), getString(R.string.history))

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 0

        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)?.view?.background = ContextCompat.getDrawable(
            this@RewardsActivity,
            com.fypmoney.R.drawable.round_backgorund_20
        )

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    tab.view.background = ContextCompat.getDrawable(
                        this@RewardsActivity,
                        com.fypmoney.R.drawable.round_backgorund_20
                    )


                } else if (tab.position == 1) {
                    tab.view.background = ContextCompat.getDrawable(
                        this@RewardsActivity,
                        com.fypmoney.R.drawable.tab_two_rewards
                    )
                    trackr {
                        it.name = TrackrEvent.open_arcade
                    }
                } else if (tab.position == 2) {
                    tab.view.background = ContextCompat.getDrawable(
                        this@RewardsActivity,
                        com.fypmoney.R.drawable.tab_third_rewards
                    )
                    trackr {
                        it.name = TrackrEvent.open_jackpot
                    }
                } else {
                    tab.view.background = ContextCompat.getDrawable(
                        this@RewardsActivity,
                        com.fypmoney.R.drawable.tab_four_rewards
                    )

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.setBackgroundColor(Color.TRANSPARENT)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })



        when (intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN)) {
            AppConstants.JACKPOTTAB -> {
                viewPager.currentItem = 2
            }


        }
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_rewards
    }

    override fun getViewModel(): RewardsAndVM {
        mViewModel = ViewModelProvider(this).get(RewardsAndVM::class.java)
        setObserver(mViewModel!!)

        return mViewModel!!
    }

    private fun setObserver(mViewModel: RewardsAndVM) {
        mViewModel?.redeemproductDetails.observe(this) {
            if (it != null) {
                mViewModel?.redeemproductDetails.postValue(null)
                Glide.with(this).asDrawable().load(it.scratchResourceHide)
                    .into(object : CustomTarget<Drawable?>() {

                        override fun onLoadCleared(@Nullable placeholder: Drawable?) {

                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?
                        ) {
                            val intent =
                                Intent(this@RewardsActivity, ScratchCardActivity::class.java)
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
                                mViewModel.orderNumber.value
                            )
                            intent.putExtra(
                                AppConstants.PRODUCT_HIDE_IMAGE,
                                it.scratchResourceShow
                            )
                            startActivity(intent)
                        }
                    })

            }


        }
    }


}
