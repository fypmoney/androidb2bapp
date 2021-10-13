package com.fypmoney.view.rewardsAndWinnings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.fypmoney.R
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardHistoryFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsOverviewFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsSpinnerListFragment
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class RewardsActivity : BaseActivity<ViewRewardsBinding, RewardsAndVM>() {


    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager


    companion object {
        var mViewModel: RewardsAndVM? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.quick_actions_rewards),
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        initializeTabs(tabLayout)


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

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)

    }

    private fun initializeTabs(tabLayout: TabLayout) {


        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(RewardsOverviewFragment(), getString(R.string.overview))
        adapter.addFragment(RewardsSpinnerListFragment(), getString(R.string.spinners))
        adapter.addFragment(RewardHistoryFragment(), getString(R.string.history))

        viewPager.adapter = adapter
//        viewPager.offscreenPageLimit=0

        tabLayout.setupWithViewPager(viewPager)

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

                } else {
                    tab.view.background = ContextCompat.getDrawable(
                        this@RewardsActivity,
                        com.fypmoney.R.drawable.tab_three_rewards
                    )

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.setBackgroundColor(Color.TRANSPARENT)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_rewards
    }

    override fun getViewModel(): RewardsAndVM {
        mViewModel = ViewModelProvider(this).get(RewardsAndVM::class.java)

        return mViewModel!!
    }


}
