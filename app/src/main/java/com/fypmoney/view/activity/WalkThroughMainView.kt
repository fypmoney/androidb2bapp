package com.fypmoney.view.activity

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
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewWalkThroughMainBinding
import com.fypmoney.view.fragment.WalkThroughFourScreen
import com.fypmoney.view.fragment.WalkThroughOneScreen
import com.fypmoney.view.fragment.WalkThroughThreeScreen
import com.fypmoney.view.fragment.WalkThroughTwoScreen
import com.fypmoney.viewmodel.WalkThroughMainViewModel
import kotlinx.android.synthetic.main.view_walk_through_main.*
import kotlinx.android.synthetic.main.view_walk_through_one.*


/*
* This class is used for WalkThrough
* */
class WalkThroughMainView : BaseActivity<ViewWalkThroughMainBinding, WalkThroughMainViewModel>() {
    private lateinit var mViewModel: WalkThroughMainViewModel
    private lateinit var mViewBinding: ViewWalkThroughMainBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_walk_through_main
    }

    override fun getViewModel(): WalkThroughMainViewModel {
        mViewModel = ViewModelProvider(this).get(WalkThroughMainViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(WalkThroughOneScreen())
        viewPagerAdapter.addFragment(WalkThroughTwoScreen())
        viewPagerAdapter.addFragment(WalkThroughThreeScreen())
        viewPagerAdapter.addFragment(WalkThroughFourScreen())
        mViewBinding.pager.offscreenPageLimit = 1
        mViewBinding.pager.adapter = viewPagerAdapter

        // here we check position of viewpager
        mViewBinding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int, positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                setButtonVisibility(position)
            }
        })
        //mViewBinding.tabLayoutIndicator.setupWithViewPager(mViewBinding.pager)
        setObserver()
    }

    /**
     *  this function is help us to change bottom button according to position
     */
    private fun setButtonVisibility(position: Int) {
        if (position == 3) {
            image2.visibility = View.GONE
            cvNext.visibility = View.VISIBLE
        } else {

            image2.visibility = View.VISIBLE
            cvNext.visibility = View.GONE
        }
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

        mViewModel.onMainClicked.observe(this)
        {
            if (mViewBinding.pager.currentItem < 3) {
                mViewBinding.pager.currentItem = mViewBinding.pager.currentItem + 1
            } else {
                intentToActivity(FirstScreenView::class.java)

            }

        }

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(this@WalkThroughMainView, aClass)
        startActivity(intent)
        finish()
    }

    // ViewPager Adapter class
    internal class ViewPagerAdapter(supportFragmentManager: FragmentManager?) :
        FragmentPagerAdapter(supportFragmentManager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mList: MutableList<Fragment> = ArrayList()
        override fun getItem(i: Int): Fragment {
            return mList[i]
        }

        override fun getCount(): Int {
            return mList.size
        }

        fun addFragment(fragment: Fragment) {
            mList.add(fragment)
        }
    }
}