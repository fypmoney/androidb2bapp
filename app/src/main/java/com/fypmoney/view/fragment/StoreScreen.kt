package com.fypmoney.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenStoreBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN

import com.fypmoney.view.interfaces.HomeTabChangeClickListener

import com.fypmoney.viewmodel.StoreScreenViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.view_notification.*


/**
 * This fragment is used for loyalty
 */
class StoreScreen(val tabchangeListner: HomeTabChangeClickListener, val whichtab: String) :
    BaseFragment<ScreenStoreBinding, StoreScreenViewModel>() {

    private lateinit var mViewModel: StoreScreenViewModel
    private lateinit var mViewBinding: ScreenStoreBinding
    private val tabIcons = intArrayOf(
        R.drawable.ic_offer_tab,
        R.drawable.ic_store_tab,
    )

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.screen_store
    }



    override fun getViewModel(): StoreScreenViewModel {
        mViewModel = ViewModelProvider(this).get(StoreScreenViewModel::class.java)
        return mViewModel
    }



    private fun initializeTabs(tabLayout: TabLayout, viewPager: ViewPager) {


//        val adapter = ViewPagerAdapter(childFragmentManager)
//        adapter.addFragment(OffersStoreActivity(), getString(R.string.offers))
//        adapter.addFragment(StoresFragment(), getString(R.string.shops))

//
//        viewPager.adapter = adapter
//
//
//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.tabRippleColor = null;
//        setupTabIcons()


    }

    private fun setupTabIcons() {
        tabLayout.getTabAt(0)!!.setIcon(tabIcons[0])
        tabLayout.getTabAt(1)!!.setIcon(tabIcons[1])

    }

    internal class ViewPagerAdapter(manager: FragmentManager?) :
        FragmentPagerAdapter(manager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel


        initializeTabs(mViewBinding.tabLayout, mViewBinding.viewPager)

        if (whichtab == AppConstants.StoreshopsScreen) {
            mViewBinding.viewPager.currentItem = 1
        } else {
            mViewBinding.viewPager.currentItem = 0
        }
        setObservers(requireContext())

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers(requireContext: Context) {

            mViewModel.onUpiClicked.observe(requireActivity()) {
                val intent = Intent(requireContext, StoreWebpageOpener2::class.java)
                intent.putExtra(ARG_WEB_PAGE_TITLE, it.title)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, it.url)
                startActivity(intent)
            }
            mViewModel.onRechargeClicked.observe(requireActivity()) {
                val intent = Intent(requireContext, StoreWebpageOpener2::class.java)
                intent.putExtra(ARG_WEB_PAGE_TITLE, it.title)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, it.url)
                startActivity(intent)
        }

    }

    override fun onTryAgainClicked() {

    }



}
