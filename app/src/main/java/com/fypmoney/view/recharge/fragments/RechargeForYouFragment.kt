package com.fypmoney.view.recharge.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentForYouPlansBinding
import com.fypmoney.view.recharge.PlanSelectionActivityDirections
import com.fypmoney.view.recharge.adapter.CircleSelectionAdapter
import com.fypmoney.view.recharge.adapter.RechargePlansAdapter
import com.fypmoney.view.recharge.model.ValueItem
import com.fypmoney.view.rewardsAndWinnings.viewModel.SpinnerFragmentVM


class RechargeForYouFragment(val list: List<ValueItem?>?, val click: (ValueItem) -> Unit) :
    BaseFragment<FragmentForYouPlansBinding, SpinnerFragmentVM>() {
    companion object {

    }

    private lateinit var mViewBinding: FragmentForYouPlansBinding
    private var mViewmodel: SpinnerFragmentVM? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_for_you_plans
    }

    override fun getViewModel(): SpinnerFragmentVM {

        mViewmodel = ViewModelProvider(this).get(SpinnerFragmentVM::class.java)



        return mViewmodel!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()


        setUpRecyclerView(list)


    }


    override fun onTryAgainClicked() {

    }


    private fun setUpRecyclerView(arrayList: List<ValueItem?>?) {
        val topTenUsersAdapter = RechargePlansAdapter(
            this, onRecentUserClick = {


                click(it)


            }
        )


        with(mViewBinding.rvPlans) {
            adapter = topTenUsersAdapter
            layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    androidx.recyclerview.widget.RecyclerView.VERTICAL,
                    false
                )
        }
        (mViewBinding.rvPlans.adapter as RechargePlansAdapter).submitList(list)

        if (arrayList?.isNotEmpty() == true) {
//
            mViewBinding?.shimmerPlans?.visibility = View.GONE
            mViewBinding?.shimmerPlans?.stopShimmer()
        }
    }


}