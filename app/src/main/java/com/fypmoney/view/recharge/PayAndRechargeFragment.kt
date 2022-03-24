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
import com.fypmoney.databinding.ActivitySelectOperatorBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.TopTenUsersAdapter
import com.fypmoney.view.home.main.explore.view.ExploreFragmentDirections
import com.fypmoney.view.home.main.home.adapter.CallToActionAdapter
import com.fypmoney.view.recharge.adapter.OperatorSelectionAdapter
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.SelectOperatorViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.collections.ArrayList


/*
* This class is used as Home Screen
* */
class PayAndRechargeFragment :
    BaseFragment<ActivitySelectOperatorBinding, SelectOperatorViewModel>() {
    private var operator: OperatorResponse? = null
    private lateinit var mViewModel: SelectOperatorViewModel
    private lateinit var mViewBinding: ActivitySelectOperatorBinding
    private val args: SelectOperatorActivityArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_pay_recharge
    }

    override fun getViewModel(): SelectOperatorViewModel {
        mViewModel = ViewModelProvider(this).get(SelectOperatorViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mViewBinding = getViewDataBinding()



        args.circle.let {
            mViewModel.circleGot.value = it
        }
        args.operator.let {
            mViewModel.OperatorGot.value = it

            mViewBinding.optionsMenu.text = it
        }

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Mobile Recharge"
        )

        setObserver()
        setBindings()


    }

    private fun setBindings() {
        mViewBinding.continueBtn.setOnClickListener {
            val directions =
                SelectOperatorActivityDirections.actionSelectCircle(
                    selectedOperator = mViewModel.operatorResponse.get()
                )

            directions?.let { it1 -> findNavController().navigate(it1) }
        }
    }

    override fun onTryAgainClicked() {

    }


    private fun setObserver() {
        val navController = findNavController();
        // We use a String here, but any type that can be put in a Bundle is supported
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<OperatorResponse>("operator_selected")
            ?.observe(
                viewLifecycleOwner
            ) { result ->
                // Do something with the result.
                var operator = result as OperatorResponse
                mViewModel.operatorResponse.set(operator)
                mViewBinding.optionsMenu.text = operator?.name

            }
        mViewBinding.optionsMenu.setOnClickListener {
            findNavController().navigate(R.id.navigation_select_operator_from_list)
        }
        mViewModel.opertaorList.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)

        }
    }


}
