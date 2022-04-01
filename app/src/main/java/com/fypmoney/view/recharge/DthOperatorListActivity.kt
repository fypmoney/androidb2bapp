package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivityOperatorListBinding
import com.fypmoney.databinding.ActivitySelectCircleBinding
import com.fypmoney.view.home.main.home.adapter.CallToActionAdapter
import com.fypmoney.view.recharge.adapter.CircleSelectionAdapter
import com.fypmoney.view.recharge.adapter.OperatorSelectionAdapter
import com.fypmoney.view.recharge.model.CircleResponse
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.DthOperatorListViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.collections.ArrayList


/*
* This class is used as Home Screen
* */
class DthOperatorListActivity :
    BaseFragment<ActivityOperatorListBinding, DthOperatorListViewModel>() {
    private lateinit var mViewModel: DthOperatorListViewModel
    private lateinit var mViewBinding: ActivityOperatorListBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_operator_list
    }

    override fun getViewModel(): DthOperatorListViewModel {
        mViewModel = ViewModelProvider(this).get(DthOperatorListViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()


        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Select your operator"
        )


        setObserver()


    }

    override fun onTryAgainClicked() {

    }

    private fun setUpRecyclerView(arrayList: ArrayList<OperatorResponse>) {
        val topTenUsersAdapter = OperatorSelectionAdapter(
            this, onRecentUserClick = {
                val directions =
                    DthOperatorListActivityDirections.actionGoToDthRecharge(
                        it.operatorId
                    )

                directions?.let { it1 -> findNavController().navigate(it1) }
            }
        )


        with(mViewBinding.rvCircles) {
            adapter = topTenUsersAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
        (mViewBinding.rvCircles.adapter as OperatorSelectionAdapter).submitList(arrayList)
    }

    private fun setObserver() {
        mViewModel.opertaorList.observe(requireActivity()) {
            setUpRecyclerView(it)
        }
    }


}
