package com.fypmoney.view.recharge


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivitySelectOperatorBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.SelectOperatorViewModel
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
* */
class SelectOperatorActivity :
    BaseFragment<ActivitySelectOperatorBinding, SelectOperatorViewModel>() {

    private val mViewModel by viewModels<SelectOperatorViewModel> { defaultViewModelProviderFactory }
    private lateinit var mViewBinding: ActivitySelectOperatorBinding
    private val args: SelectOperatorActivityArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_select_operator
    }

    override fun getViewModel(): SelectOperatorViewModel {
        return mViewModel
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

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
        setBindings()
        setObserver()

        args.circle.let {
            mViewModel.circleGot.value = it
        }
        args.mobile.let {
            mViewModel.mobileNumber.value = it

            mViewBinding.tvUserNumber.text = it
        }
        args.operator.let {
            mViewModel.OperatorGot.value = it

//            mViewBinding.optionsMenu.text = it
        }


        args.rechargeType.let {
            mViewModel.rechargeType.value = it

            if (it == AppConstants.POSTPAID) {
                mViewModel.callGetOperatorList(AppConstants.POSTPAID)
            } else {

                mViewModel.callGetOperatorList(AppConstants.PREPAID)
            }
        }

    }

    private fun setBindings() {
        mViewBinding.continueBtn.setOnClickListener {
            if (mViewModel.operatorResponse.get() != null) {
                val directions =
                    SelectOperatorActivityDirections.actionSelectCircle(
                        selectedOperator = mViewModel.operatorResponse.get(),
                        mobile = mViewModel.mobileNumber.value,
                        rechargeType = mViewModel.rechargeType.value
                    )

                directions.let { it1 -> findNavController().navigate(it1) }
            } else {
                Utility.showToast("fetch details")
            }

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

            val directions =
                SelectOperatorActivityDirections.actionToOperatorList(
                    rechargeType = mViewModel.rechargeType.value
                )

            directions.let { it1 -> findNavController().navigate(it1) }

        }
        mViewModel.opertaorList.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)

        }
    }


}
