package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivitySelectCircleBinding
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
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Mobile Recharge"
        )


        setObserver()


    }

    override fun onTryAgainClicked() {

    }

    private fun setUpRecyclerView(arrayList: ArrayList<CircleResponse>) {
        val topTenUsersAdapter = CircleSelectionAdapter(
            this, onRecentUserClick = {

                findNavController().navigate(R.id.navigation_select_operator)
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
