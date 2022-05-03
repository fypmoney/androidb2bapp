package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.SelectOperatorFragmentBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.POSTPAID
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.adapter.OperatorSelectionAdapter
import com.fypmoney.view.recharge.viewmodel.SelectOperatorFragmentVM
import kotlinx.android.synthetic.main.toolbar.*


class SelectOperatorFragment : BaseFragment<SelectOperatorFragmentBinding, SelectOperatorFragmentVM>() {

    private val mViewModel by viewModels<SelectOperatorFragmentVM> { defaultViewModelProviderFactory }

    private lateinit var mViewBinding: SelectOperatorFragmentBinding


    private val args: SelectOperatorFragmentArgs by navArgs()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.select_operator_fragment
    }

    override fun getViewModel(): SelectOperatorFragmentVM {
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewModel.rechargeType = args.rechargeType
        mViewModel.mobileNo = args.mobileNo
        setUpRecyclerView()
        setObserver()
        mViewModel.rechargeType?.let {
            setToolbarAndTitle(
                context = requireContext(),
                toolbar = toolbar, backArrowTint = Color.WHITE,
                titleColor = Color.WHITE,
                isBackArrowVisible = true,
                toolbarTitle = String.format(getString(R.string.select_your_prepaid_operator),Utility.toTitleCase(it))
            )
            if (it == AppConstants.POSTPAID) {
                mViewModel.callGetOperatorList(AppConstants.POSTPAID)
            } else {
                mViewModel.callGetOperatorList(AppConstants.PREPAID)
            }
        }



    }

    override fun onTryAgainClicked() {

    }

    private fun setUpRecyclerView() {
        val operatorAdapter = OperatorSelectionAdapter(
            this, onOperatorClick = {
                if(it.id =="postpaid"){
                    val directions =
                        SelectOperatorFragmentDirections.actionRechargeScreen(rechargeType = AppConstants.POSTPAID)
                    directions.let { it1 -> findNavController().navigate(it1) }
                }else if(it.id=="prepaid"){
                    val directions =
                        SelectOperatorFragmentDirections.actionRechargeScreen(rechargeType = AppConstants.PREPAID)
                    directions.let { it1 -> findNavController().navigate(it1) }
                }else{
                    if(mViewModel.rechargeType== POSTPAID){
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            "operator_selected",
                            it
                        )
                        findNavController().popBackStack()
                    }else{
                        val directions =
                            SelectOperatorFragmentDirections.actionOperatorToSelectCircle(
                                selectedOperator = it,
                                mobile = mViewModel.mobileNo,
                                rechargeType = mViewModel.rechargeType
                            )

                        directions.let { it1 -> findNavController().navigate(it1) }
                    }
                }

            }
        )
        with(mViewBinding.rvOperators) {
            adapter = operatorAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.HORIZONTAL
                )
            )
        }
    }

    private fun setObserver() {
        mViewModel.state.observe(viewLifecycleOwner){
            handelState(it)
        }
    }

    private fun handelState(it: SelectOperatorFragmentVM.SelectOperatorState?) {
        when(it){
            is SelectOperatorFragmentVM.SelectOperatorState.Error -> {
                mViewBinding.noOperatorFoundTv.toVisible()
                mViewBinding.shimmerLayout.toGone()
                mViewBinding.rvOperators.toGone()
            }
            SelectOperatorFragmentVM.SelectOperatorState.Loading -> {
                mViewBinding.noOperatorFoundTv.toGone()
                mViewBinding.shimmerLayout.toVisible()
                mViewBinding.rvOperators.toGone()
            }
            is SelectOperatorFragmentVM.SelectOperatorState.Success -> {
                mViewBinding.noOperatorFoundTv.toGone()
                mViewBinding.shimmerLayout.toGone()
                mViewBinding.rvOperators.toVisible()
                (mViewBinding.rvOperators.adapter as OperatorSelectionAdapter).submitList(it.operatorList)


            }
            null -> TODO()
        }
    }


}
