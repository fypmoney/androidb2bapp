package com.fypmoney.view.recharge


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivitySelectCircleBinding
import com.fypmoney.view.home.main.explore.sectionexplore.SectionExploreFragmentArgs
import com.fypmoney.view.home.main.explore.sectionexplore.SectionExploreFragmentVM
import com.fypmoney.view.home.main.home.adapter.CallToActionAdapter
import com.fypmoney.view.recharge.adapter.CircleSelectionAdapter
import com.fypmoney.view.recharge.adapter.OperatorSelectionAdapter
import com.fypmoney.view.recharge.model.CircleResponse
import com.fypmoney.view.recharge.viewmodel.SelectCircleViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.collections.ArrayList


/*
* This class is used as Home Screen
* */
class SelectCircleActivity : BaseFragment<ActivitySelectCircleBinding, SelectCircleViewModel>() {
    private lateinit var mViewModel: SelectCircleViewModel
    private lateinit var mViewBinding: ActivitySelectCircleBinding
    private val args: SelectCircleActivityArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_select_circle
    }

    override fun getViewModel(): SelectCircleViewModel {
        mViewModel = ViewModelProvider(this).get(SelectCircleViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.selectedOperator.value = args.selectedOperator
        mViewModel.mobile.value = args.mobile
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Mobile Recharge"
        )

        mViewModel.callGetOperatorList()
        setObserver()


    }

    override fun onTryAgainClicked() {

    }

    private fun setUpRecyclerView(arrayList: ArrayList<CircleResponse>) {
        val topTenUsersAdapter = CircleSelectionAdapter(
            this, onRecentUserClick = {
                val directions =
                    it.name?.let { it1 ->
                        mViewModel.mobile.value?.let { it2 ->
                            SelectCircleActivityDirections.actionSelectRechargePlans(

                                mViewModel.selectedOperator.value,
                                selectedCircle = it1,
                                mobile = it2
                            )
                        }
                    }

                directions?.let { it1 -> findNavController().navigate(it1) }

            }
        )


        with(mViewBinding.rvCircles) {
            adapter = topTenUsersAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
        (mViewBinding.rvCircles.adapter as CircleSelectionAdapter).submitList(arrayList)
    }

    private fun setObserver() {
        mViewModel.opertaorList.observe(requireActivity()) {
            setUpRecyclerView(it)
        }
    }


}
