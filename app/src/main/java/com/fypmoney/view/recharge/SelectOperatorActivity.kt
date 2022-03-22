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
import com.fypmoney.databinding.ActivitySelectOperatorBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.TopTenUsersAdapter
import com.fypmoney.view.home.main.home.adapter.CallToActionAdapter
import com.fypmoney.view.recharge.adapter.OperatorSelectionAdapter
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.SelectOperatorViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.collections.ArrayList


/*
* This class is used as Home Screen
* */
class SelectOperatorActivity :
    BaseFragment<ActivitySelectOperatorBinding, SelectOperatorViewModel>() {
    private lateinit var mViewModel: SelectOperatorViewModel
    private lateinit var mViewBinding: ActivitySelectOperatorBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_select_operator
    }

    override fun getViewModel(): SelectOperatorViewModel {
        mViewModel = ViewModelProvider(this).get(SelectOperatorViewModel::class.java)
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


    private fun setObserver() {

        mViewBinding.optionsMenu.setOnClickListener {
            findNavController().navigate(R.id.navigation_select_operator_from_list)
        }
        mViewModel.opertaorList.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)

        }
    }


}
