package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.RechargePlansFragmentBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.fragments.RechargeForYouFragment
import com.fypmoney.view.recharge.model.RechargePlansResponse
import com.fypmoney.view.recharge.viewmodel.RechargePlansFragmentVM
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi


@ObsoleteCoroutinesApi
@FlowPreview
class RechargePlansFragment : BaseFragment<RechargePlansFragmentBinding, RechargePlansFragmentVM>() {
    private  val rechargePlansFragmentVM by viewModels<RechargePlansFragmentVM> { defaultViewModelProviderFactory }

    private lateinit var mViewBinding: RechargePlansFragmentBinding

    private val args: RechargePlansFragmentArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.recharge_plans_fragment
    }

    override fun getViewModel(): RechargePlansFragmentVM = rechargePlansFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true, toolbarTitle = "${args.selectedOperator?.name} ${args.selectedCircle}"
        )

        rechargePlansFragmentVM.selectedOperator.value = args.selectedOperator
        rechargePlansFragmentVM.selectedCircle.value = args.selectedCircle
        rechargePlansFragmentVM.rechargeType = args.rechargeTye
        rechargePlansFragmentVM.mobile.value = args.mobile
        mViewBinding.tvUserNumber.text = args.mobile

        when (args.selectedOperator?.name) {
            "Airtel" -> {
                mViewBinding.operatorIv.setBackgroundResource(R.drawable.ic_airtel)
            }
            "Vodafone" -> {
                mViewBinding.operatorIv.setBackgroundResource(R.drawable.ic_vodafone)
            }
            "JIO" -> {
                mViewBinding.operatorIv.setBackgroundResource(R.drawable.ic_jio)
            }
        }
        setObserver()

        rechargePlansFragmentVM.callRechargePlan()

    }

    private fun setObserver() {
        rechargePlansFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
    }

    private fun handelState(it: RechargePlansFragmentVM.RechargePlanState?) {
        when(it){
            is RechargePlansFragmentVM.RechargePlanState.Error -> {
                mViewBinding.noPlanFoundTv.toVisible()
                mViewBinding.planDataCl.toGone()
                mViewBinding.shimmerPlans.toGone()
            }
            RechargePlansFragmentVM.RechargePlanState.Loading -> {
                mViewBinding.planDataCl.toGone()
                mViewBinding.shimmerPlans.toVisible()
            }
            is RechargePlansFragmentVM.RechargePlanState.Success -> {
                mViewBinding.planDataCl.toVisible()
                mViewBinding.shimmerPlans.toGone()
                initializeTabs(mViewBinding.tabLayout, it.plans)
            }
            null -> {}
        }
    }

    override fun onTryAgainClicked() {

    }

    fun setupViews(){

    }
    private fun initializeTabs(
        tabLayout: TabLayout,
        rechargePlansResponse: List<RechargePlansResponse>,
    ) {
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.clearFragment()
        rechargePlansResponse.forEach {
            it.name?.let { it1 ->
                Utility.toTitleCase(it1)?.let { it2 ->
                    adapter.addFragment(RechargeForYouFragment(list = it.value,
                        click = { valueItem->
                            trackr {it1->
                                it1.name = TrackrEvent.prepaid_choose_plan
                            }
                            if(findNavController().currentDestination?.id ==R.id.navigation_select_plans){
                                val directions = RechargePlansFragmentDirections.actionRechargePlanToSelectedPlan(
                                    valueItem,
                                    rechargePlansFragmentVM.selectedOperator.value,
                                    mobile = rechargePlansFragmentVM.mobile.value,
                                    planType = it1,
                                    rechargeType = rechargePlansFragmentVM.rechargeType,
                                    circle = rechargePlansFragmentVM.selectedCircle.value
                                )

                                findNavController().navigate(directions)
                            }

                        }), it2
                    )
                }
            }
        }
        mViewBinding.viewPager.adapter = adapter
        tabLayout.setupWithViewPager(mViewBinding.viewPager)

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
        fun clearFragment(){
            mFragmentList.clear()
            mFragmentTitleList.clear()
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }


}
