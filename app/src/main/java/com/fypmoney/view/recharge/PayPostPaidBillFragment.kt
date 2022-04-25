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
import com.fypmoney.databinding.ActivityPostpaidRechargeBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.PayBillViewModel
import kotlinx.android.synthetic.main.activity_dth_recharge.*
import kotlinx.android.synthetic.main.toolbar.*


class PayPostPaidBillFragment :
    BaseFragment<ActivityPostpaidRechargeBinding, PayBillViewModel>() {

    private lateinit var mViewModel: PayBillViewModel
    private lateinit var mViewBinding: ActivityPostpaidRechargeBinding
    private val args: PayPostPaidBillFragmentArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_postpaid_recharge
    }

    override fun getViewModel(): PayBillViewModel {
        mViewModel = ViewModelProvider(this).get(PayBillViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()



        args.selectedCircle.let {
            mViewModel.circleGot.value = it
        }

        args.selectedOperator.let {
            mViewModel.operatorResponse.set(it)
            mViewBinding.optionsMenu.text = it?.name

//            mViewBinding.optionsMenu.text = it
        }
        args.mobile.let {
            mViewModel.mobileNumber.value = it
            if (it != null) {
                mViewModel.callGetDthInfo(
                    it
                )
            }

            mViewBinding.tvUserNumber.text = it
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

            if (!mViewBinding.enterBillAmount.text.isNullOrEmpty()) {
                if (!mViewModel.mobileNumber.value.isNullOrEmpty() && mViewModel.mobileNumber.value!!.length == 10) {
                    mViewModel.callMobileRecharge(
                        mViewModel.operatorResponse.get()?.operatorId,
                        mViewModel.mobileNumber.value!!,
                        mViewBinding.enterBillAmount.text.toString()
                    )
                } else {
                    Utility.showToast("Enter correct number")
                }
            } else {
                Utility.showToast("Enter bill amount")
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
                mViewBinding.optionsMenu.text = operator.name

            }
        mViewBinding.optionsMenu.setOnClickListener {
            findNavController().navigate(R.id.navigation_select_operator_from_list)
        }

        mViewModel.fetchedBill.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)
            if (it.amount != null) {
                mViewBinding.enterBillAmount.setText(
                    it.amount?.toDoubleOrNull()?.toInt().toString()
                )
            }

            if (it.bill_fetch?.billdate != null) {
                mViewBinding.billDate.setText("Billing date " + it.bill_fetch?.billdate)
            } else {
                mViewBinding.billDate.setText("Billing fetch failed")
            }

            if (it.bill_fetch?.dueDate != null) {
                mViewBinding.textView10.setText("Due date is " + it.bill_fetch?.dueDate)
            } else {
                mViewBinding.textView10.setText("Enter amount manually")
            }


        }
        mViewModel.paymentResponse.observe(viewLifecycleOwner) {

            it?.let {
                val directions = PayPostPaidBillFragmentDirections.actionRechargeSuccess(
                    successDth = it,
                    selectedOperator = mViewModel.operatorResponse.get(),

                    )
                findNavController().navigate(directions)

                mViewModel.paymentResponse.value = null
            }


        }

        mViewModel.failedRecharge.observe(viewLifecycleOwner) {

            it?.let {

                mViewModel.failedRecharge.value = null
                val directions = PayPostPaidBillFragmentDirections.actionRechargeSuccess(
                    successDth = null,
                    selectedOperator = mViewModel.operatorResponse.get(),
                    amount = mViewBinding.enterBillAmount.text.toString(),
                    mobile = mViewModel.mobileNumber.value

                )
                findNavController().navigate(directions)


            }


        }


        mViewModel.opertaorList.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)

        }


    }


}
