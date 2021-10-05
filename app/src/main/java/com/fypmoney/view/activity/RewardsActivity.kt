package com.fypmoney.view.activity

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

import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.*

import com.google.android.material.tabs.TabLayout

import java.util.ArrayList


import com.fypmoney.databinding.ViewRewardsBinding
import com.fypmoney.viewmodel.RewardsViewModel
import kotlinx.android.synthetic.main.toolbar.*

import androidx.core.content.ContextCompat
import com.fypmoney.R
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class RewardsActivity : BaseActivity<ViewRewardsBinding, RewardsViewModel>() {


    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager


    companion object {
        var mViewModel: RewardsViewModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        setObserver()
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

    private fun initializeTabs(tabLayout: TabLayout) {


        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(RewardsOverviewFragment(), getString(R.string.overview))
        adapter.addFragment(RewardsSpinnerListFragment(), getString(R.string.spinners))
        adapter.addFragment(RewardHistoryFragment(), getString(R.string.history))

        viewPager.adapter = adapter

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

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

    }


    private fun intentToPayActivity(aClass: Class<*>, pay: String? = null) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, pay)
        startActivity(intent)
    }


    private fun callActivity(aClass: Class<*>, amount: String?) {
        val intent = Intent(this, aClass)
        intent.putExtra("amountshouldbeadded", amount)



        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 99) {


        }


    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_rewards
    }

    override fun getViewModel(): RewardsViewModel {
        mViewModel = ViewModelProvider(this).get(RewardsViewModel::class.java)

        return mViewModel!!
    }
}
