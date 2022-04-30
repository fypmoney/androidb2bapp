package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
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
import com.fypmoney.view.rewardsAndWinnings.RewardsActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi


@ObsoleteCoroutinesApi
@FlowPreview
class RechargePlansFragment : BaseFragment<RechargePlansFragmentBinding, RechargePlansFragmentVM>() {
    private lateinit var mViewModel: RechargePlansFragmentVM
    private lateinit var mViewBinding: RechargePlansFragmentBinding
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager

    private val args: RechargePlansFragmentArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.recharge_plans_fragment
    }

    override fun getViewModel(): RechargePlansFragmentVM {
        mViewModel = ViewModelProvider(this).get(RechargePlansFragmentVM::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true, toolbarTitle = "${args.selectedOperator?.name} ${args.selectedCircle}"
        )

        mViewModel.selectedOperator.value = args.selectedOperator
        mViewModel.selectedCircle.value = args.selectedCircle
        mViewModel.mobile.value = args.mobile
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
        tabLayout = mViewBinding.tabLayout
        viewPager = mViewBinding.viewPager
        setObserver()

        mViewModel.callRechargePlan()

    }

    private fun setObserver() {
        mViewModel.state.observe(viewLifecycleOwner){
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
                initializeTabs(tabLayout, it.plans)

            }
            null -> TODO()
        }
    }

    override fun onTryAgainClicked() {

    }


    private fun initializeTabs(
        tabLayout: TabLayout,
        rechargePlansResponse: List<RechargePlansResponse>
    ) {
        val adapter = RewardsActivity.ViewPagerAdapter(childFragmentManager)
        rechargePlansResponse.forEach {
            it.name?.let { it1 ->
                Utility.toTitleCase(it1)?.let { it2 ->
                    adapter.addFragment(RechargeForYouFragment(it.value,
                        click = {
                            val directions = RechargePlansFragmentDirections.actionRechargeAndPay(
                                it,
                                mViewModel.selectedOperator.value,
                                mobile = mViewModel.mobile.value,
                                planType = it1
                            )

                            findNavController().navigate(directions)
                        }), it2
                    )
                }
            }
        }
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }


}
