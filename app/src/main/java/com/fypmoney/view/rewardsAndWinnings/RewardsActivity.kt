package com.fypmoney.view.rewardsAndWinnings


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewRewardsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.*
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardHistoryFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsJackpotFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsSpinnerListFragment
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


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


    }

    private fun initializeTabs(tabLayout: TabLayout, intent: Intent) {


        val adapter = ViewPagerAdapter(supportFragmentManager)

//        adapter.addFragment(RewardsOverviewFragment(), getString(R.string.overview))

        adapter.addFragment(RewardsSpinnerListFragment.newInstance(), getString(R.string.arcade))
        adapter.addFragment(RewardsJackpotFragment.newInstance(), getString(R.string.jackpot))
        adapter.addFragment(RewardHistoryFragment.newInstance(), getString(R.string.history))

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


        return mViewModel!!
    }




}
