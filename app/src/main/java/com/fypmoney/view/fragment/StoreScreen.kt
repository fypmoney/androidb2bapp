package com.fypmoney.view.fragment

import com.fypmoney.view.StoreWebpageOpener
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
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenStoreBinding
import com.fypmoney.model.StoreDataModel
import com.fypmoney.viewmodel.StoreScreenViewModel
import com.google.android.material.tabs.TabLayout
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


/**
 * This fragment is used for loyalty
 */
class StoreScreen : BaseFragment<ScreenStoreBinding, StoreScreenViewModel>() {

    private lateinit var mViewModel: StoreScreenViewModel
    private lateinit var mViewBinding: ScreenStoreBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.screen_store
    }

    public fun StoreScreen() {

    }

    override fun getViewModel(): StoreScreenViewModel {
        mViewModel = ViewModelProvider(this).get(StoreScreenViewModel::class.java)
        return mViewModel
    }


    private fun initializeTabs(tabLayout: TabLayout, viewPager: ViewPager) {


        val adapter = ViewPagerAdapter(childFragmentManager)

        adapter.addFragment(StoresFragment(), "Shops")
        adapter.addFragment(OffersStoreFragment(), "Offers")


        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager);


    }

    internal class ViewPagerAdapter(manager: FragmentManager?) :
        FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = java.util.ArrayList()
        private val mFragmentTitleList: MutableList<String> = java.util.ArrayList()
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

        setObservers(requireContext())

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers(requireContext: Context) {

            mViewModel.onUpiClicked.observe(requireActivity()) {


                val intent2 = Intent(requireContext, StoreWebpageOpener::class.java)
                StoreWebpageOpener.url = it.url!!
                intent2.putExtra("title", it.title)
                requireContext.startActivity(intent2)


            }
        mViewModel.onRechargeClicked.observe(requireActivity()) {


            val intent2 = Intent(requireContext, StoreWebpageOpener::class.java)
            StoreWebpageOpener.url = it.url!!
            intent2.putExtra("title", it.title)
            requireContext.startActivity(intent2)


        }

    }

    override fun onTryAgainClicked() {

    }



}
