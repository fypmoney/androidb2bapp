package com.fypmoney.view.home.main.rewards.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRewardsBinding
import com.fypmoney.view.home.main.rewards.viewmodel.RewardsFragmentVM
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.util.AppConstants
import com.fypmoney.view.rewardsAndWinnings.RewardsActivity
import com.fypmoney.view.rewardsAndWinnings.activity.ScratchCardActivity
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardHistoryFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsJackpotFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsOverviewFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsSpinnerListFragment
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.ArrayList

class RewardsFragment : BaseFragment<FragmentRewardsBinding,RewardsAndVM>() {

    private  val rewardsviewModel by viewModels<RewardsAndVM> {
        defaultViewModelProviderFactory
    }
    private lateinit var _binding: FragmentRewardsBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    private var dialogInsufficientFund: Dialog? = null

    override fun onTryAgainClicked() {
        TODO("Not yet implemented")
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int  = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int  = R.layout.fragment_rewards

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): RewardsAndVM  = rewardsviewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()

        dialogInsufficientFund = Dialog(requireContext())

        initializeTabs(binding.tabLayout, requireActivity().intent)



        rewardsviewModel.totalmyntsClicked.observe(
            viewLifecycleOwner,
            { list ->
                if (list) {
                    binding.viewPager.currentItem = 3
                    rewardsviewModel.totalmyntsClicked.postValue(false)

                }
            }
        )
        rewardsviewModel.error.observe(
            viewLifecycleOwner,
            { list ->
                if (list.errorCode == "PKT_2051") {

                    callInsuficientFundDialog(list.msg)
                }

            }
        )
        setObserver(rewardsviewModel)

    }
    private fun initializeTabs(tabLayout: TabLayout, intent: Intent) {


        val adapter = ViewPagerAdapter(childFragmentManager)

        adapter.addFragment(RewardsOverviewFragment(), getString(R.string.overview))

        adapter.addFragment(RewardsSpinnerListFragment(), getString(R.string.arcade))
        adapter.addFragment(RewardsJackpotFragment(), getString(R.string.jackpot))
        adapter.addFragment(RewardHistoryFragment(), getString(R.string.history))

        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 0

        tabLayout.setupWithViewPager(binding.viewPager)
        tabLayout.getTabAt(0)?.view?.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.round_backgorund_20
        )

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    tab.view.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_backgorund_20
                    )


                } else if (tab.position == 1) {
                    tab.view.background = ContextCompat.getDrawable(
                        requireContext(),
                        com.fypmoney.R.drawable.tab_two_rewards
                    )
                    trackr {
                        it.name = TrackrEvent.open_arcade
                    }
                } else if (tab.position == 2) {
                    tab.view.background = ContextCompat.getDrawable(
                        requireContext(),
                        com.fypmoney.R.drawable.tab_third_rewards
                    )
                    trackr {
                        it.name = TrackrEvent.open_jackpot
                    }
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
        rewardsviewModel.callTotalRewardsEarnings()
        rewardsviewModel.callRewardSummary()
        rewardsviewModel.callRewardHistory()


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
                                Intent(requireContext(), ScratchCardActivity::class.java)
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