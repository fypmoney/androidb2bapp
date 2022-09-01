package com.fypmoney.view.rewardsAndWinnings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRewardsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.home.main.rewards.ViewPagerFragmentAdapter
import com.fypmoney.view.home.main.rewards.viewmodel.RewardsFragmentVM
import com.fypmoney.view.interfaces.HomeTabChangeClickListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*


class RewardsFragment : BaseFragment<FragmentRewardsBinding, RewardsFragmentVM>() {

    private val rewardsviewModel by viewModels<RewardsFragmentVM> {
        defaultViewModelProviderFactory
    }
    private lateinit var _binding: FragmentRewardsBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding


    override fun onTryAgainClicked() {
        TODO("Not yet implemented")
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.fragment_rewards

    /**
     * Override for set view model
     *
     * @return view model instance
     */


    var myViewPager2: ViewPager2? = null
    var myAdapter: ViewPagerFragmentAdapter? = null
    private val arrayList = ArrayList<Fragment>()
    override fun getViewModel(): RewardsFragmentVM = rewardsviewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()


        myViewPager2 = _binding.viewPager
        Handler(Looper.getMainLooper()).postDelayed({
            try {

                initializeTabs(_binding.tabLayout, requireActivity().intent)
                _binding.loader.visibility = View.GONE

            } catch (e: Exception) {

            }

        }, 150)


    }

    private fun initializeTabs(tabLayout: TabLayout, intent: Intent) {

//
//        val adapter = ViewPagerAdapter(childFragmentManager)
//
//        adapter.addFragment(RewardsOverviewFragment(), getString(R.string.overview))
//
//        adapter.addFragment(RewardsSpinnerListFragment(), getString(R.string.arcade))
//        adapter.addFragment(RewardsJackpotFragment(), getString(R.string.jackpot))
//        adapter.addFragment(RewardHistoryFragment(), getString(R.string.history))
//
//        binding.viewPager.adapter = adapter
//        binding.viewPager.offscreenPageLimit = 0


        // add Fragments in your ViewPagerFragmentAdapter class

        // add Fragments in your ViewPagerFragmentAdapter class
        //arrayList.add(RewardsOverviewFragment.newInstance(tabchangeListner))
        /*
        arrayList.add(RewardsSpinnerListFragment.newInstance())
        arrayList.add(RewardsJackpotFragment.newInstance())
        arrayList.add(RewardHistoryFragment.newInstance())*/


        myAdapter = ViewPagerFragmentAdapter(childFragmentManager, lifecycle, arrayList)
        // set Orientation in your ViewPager2
        // set Orientation in your ViewPager2
        myViewPager2!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        myViewPager2!!.adapter = myAdapter

        myViewPager2!!.setPageTransformer(MarginPageTransformer(1500))
        TabLayoutMediator(tabLayout, myViewPager2!!) { tab, position ->


            tab.text = when (position) {
                0 -> {
                    getString(R.string.overview)
                }
                1 -> getString(R.string.arcade)
                2 -> getString(R.string.jackpot)
                3 -> getString(R.string.history)
                else -> getString(R.string.overview)
            }

        }.attach()


        tabLayout.getTabAt(0)?.view?.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.round_backgorund_20
        )

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    tab.view.background = ContextCompat.getDrawable(
                        requireContext(),
                        com.fypmoney.R.drawable.round_backgorund_20
                    )


                } else if (tab.position == 1) {
                    tab.view.background = ContextCompat.getDrawable(
                        requireContext(),
                        com.fypmoney.R.drawable.tab_two_rewards
                    )
//                    trackr {
//                        it.name = TrackrEvent.open_arcade
//                    }
                } else if (tab.position == 2) {
                    tab.view.background = ContextCompat.getDrawable(
                        requireContext(),
                        com.fypmoney.R.drawable.tab_third_rewards
                    )
//                    trackr {
//                        it.name = TrackrEvent.open_jackpot
//                    }
                } else {
                    tab.view.background = ContextCompat.getDrawable(
                        requireContext(),
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
                binding.viewPager.currentItem = 2
            }


        }
    }


}