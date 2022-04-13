package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivityOperatorListBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.recharge.adapter.OperatorSelectionAdapter
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.SelectOperatorFromListViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.collections.ArrayList


/*
* This class is used as Home Screen
* */
class SelectOperatorFromListActivity :
    BaseFragment<ActivityOperatorListBinding, SelectOperatorFromListViewModel>() {
    private lateinit var mViewModel: SelectOperatorFromListViewModel
    private lateinit var mViewBinding: ActivityOperatorListBinding


    private val args: SelectOperatorFromListActivityArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_operator_list
    }

    override fun getViewModel(): SelectOperatorFromListViewModel {
        mViewModel = ViewModelProvider(this).get(SelectOperatorFromListViewModel::class.java)
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
        args.rechargeType.let {
            mViewModel.rechargeType.value = it

            if (it == AppConstants.POSTPAID) {
                mViewModel.callGetOperatorList(AppConstants.POSTPAID)
            } else {

                mViewModel.callGetOperatorList(AppConstants.PREPAID)
            }


//            mViewBinding.optionsMenu.text = it
        }

        setObserver()


    }

    override fun onTryAgainClicked() {

    }

    private fun setUpRecyclerView(arrayList: ArrayList<OperatorResponse>) {
        val topTenUsersAdapter = OperatorSelectionAdapter(
            this, onRecentUserClick = {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    "operator_selected",
                    it
                )
                findNavController().popBackStack()
            }
        )


        with(mViewBinding.rvCircles) {
            adapter = topTenUsersAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
        (mViewBinding.rvCircles.adapter as OperatorSelectionAdapter).submitList(arrayList)
    }

    private fun setObserver() {
        mViewModel.opertaorList.observe(requireActivity()) {
            setUpRecyclerView(it)
        }
    }


}
