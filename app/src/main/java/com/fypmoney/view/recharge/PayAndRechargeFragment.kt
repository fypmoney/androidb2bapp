package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivityPayRechargeBinding
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.PayAndRechargeViewModel
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
* */
class PayAndRechargeFragment :
    BaseFragment<ActivityPayRechargeBinding, PayAndRechargeViewModel>() {
    private var operator: OperatorResponse? = null
    private lateinit var mViewModel: PayAndRechargeViewModel
    private lateinit var mViewBinding: ActivityPayRechargeBinding
    private val args: PayAndRechargeFragmentArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_pay_recharge
    }

    override fun getViewModel(): PayAndRechargeViewModel {
        mViewModel = ViewModelProvider(this).get(PayAndRechargeViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mViewBinding = getViewDataBinding()


        mViewModel.selectedPlan.value = args.selectedPlan

        mViewModel.operatorResponse.value = args.selectedOperator
        mViewModel.mobile.value = args.mobile
        mViewModel.planType.value = args.planType




        mViewBinding.amountTv.text = "Rs " + args.selectedPlan?.rs
        mViewBinding.details.text = args.selectedPlan?.desc

        mViewBinding.planType.text = args.planType

        mViewBinding.tvUserNumber.text = args.mobile

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
            mViewModel.callMobileRecharge(
                mViewModel.selectedPlan.value,
                mViewModel.mobile.value,
                mViewModel.operatorResponse.value,
                mViewModel.planType.value
            )
        }
    }

    override fun onTryAgainClicked() {

    }


    private fun setObserver() {



        mViewModel.success.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)

            it?.let {

                mViewModel.success.postValue(null)


                var directions = PayAndRechargeFragmentDirections.actionRechargeSuccess(
                    successResponse = it,
                    selectedOperator = mViewModel.operatorResponse.value,
                    amount = args.selectedPlan?.rs,
                    mobile = mViewModel.mobile.value

                )
                findNavController().navigate(directions)


            }
        }


        mViewModel.failedresponse.observe(viewLifecycleOwner) {
            it?.let {

                mViewModel.failedresponse.postValue(null)

                if (it == "failed") {
                    var directions = PayAndRechargeFragmentDirections.actionRechargeSuccess(
                        successResponse = null,
                        selectedOperator = mViewModel.operatorResponse.value,

                        )
                    findNavController().navigate(directions)

                }


            }
        }

    }


}
