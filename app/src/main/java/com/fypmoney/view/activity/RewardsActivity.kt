package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity

import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.*
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.fypmoney.view.interfaces.MessageSubmitClickListener

import com.google.android.material.tabs.TabLayout

import java.util.ArrayList


import android.app.ActivityOptions
import com.fypmoney.databinding.ViewRewardsBinding
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.RewardsViewModel
import kotlinx.android.synthetic.main.toolbar.*


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

        adapter.addFragment(RewardsOverviewFragment(), "Overview")
        adapter.addFragment(RewardHistoryFragment(), "History")
        adapter.addFragment(RewardsSpinnerListFragment(), "Spinners")


        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager);


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
