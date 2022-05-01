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
import com.fypmoney.databinding.PostpaidBillDetailsRechargeFragmentBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.viewmodel.PostpaidBillDetailsFragmentVM
import kotlinx.android.synthetic.main.toolbar.*


class PostpaidBillDetailsRechargeFragment : BaseFragment<PostpaidBillDetailsRechargeFragmentBinding, PostpaidBillDetailsFragmentVM>() {

    private lateinit var postpaidBillDetailsFragmentVM: PostpaidBillDetailsFragmentVM
    private lateinit var binding: PostpaidBillDetailsRechargeFragmentBinding
    private val args: PostpaidBillDetailsRechargeFragmentArgs by navArgs()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.postpaid_bill_details_recharge_fragment
    }

    override fun getViewModel(): PostpaidBillDetailsFragmentVM {
        postpaidBillDetailsFragmentVM = ViewModelProvider(this).get(PostpaidBillDetailsFragmentVM::class.java)
        return postpaidBillDetailsFragmentVM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()

        args.selectedCircle.let {
            postpaidBillDetailsFragmentVM.circleGot.value = it
        }
        args.mobile.let {
            postpaidBillDetailsFragmentVM.mobileNumber.value = it
            if (it != null) {
                postpaidBillDetailsFragmentVM.callGetDthInfo(
                    it
                )
            }

            binding.mobileNumberTv.text = it
        }
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Bill Payment"
        )

        setObserver()
        setBindings()


    }

    private fun setBindings() {
        binding.continueBtn.setOnClickListener {

            if (!binding.amountEt.text.isNullOrEmpty()) {
                if (!postpaidBillDetailsFragmentVM.mobileNumber.value.isNullOrEmpty() && postpaidBillDetailsFragmentVM.mobileNumber.value!!.length == 10) {
                    postpaidBillDetailsFragmentVM.callMobileRecharge(
                        postpaidBillDetailsFragmentVM.operatorResponse.get()?.operatorId,
                        postpaidBillDetailsFragmentVM.mobileNumber.value!!,
                        binding.amountEt.text.toString()
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
        /*val navController = findNavController();
        // We use a String here, but any type that can be put in a Bundle is supported
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<OperatorResponse>("operator_selected")
            ?.observe(
                viewLifecycleOwner
            ) { result ->
                // Do something with the result.
                var operator = result as OperatorResponse
                mViewModel.operatorResponse.set(operator)
                mViewBinding.optionsMenu.text = operator.name

            }*/

        postpaidBillDetailsFragmentVM.fetchedBill.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)
            if (it.amount != null) {
                binding.amountEt.setText(
                    it.amount?.toDoubleOrNull()?.toInt().toString()
                )
            }

            if (it.bill_fetch?.billdate != null) {
                binding.billDueDateTv.setText("Billing date " + it.bill_fetch?.billdate)
            } else {
                binding.billDueDateTv.setText("Billing fetch failed")
            }

           /* if (it.bill_fetch?.dueDate != null) {
                mViewBinding.textView10.setText("Due date is " + it.bill_fetch?.dueDate)
            } else {
                mViewBinding.textView10.setText("Enter amount manually")
            }*/


        }
        postpaidBillDetailsFragmentVM.paymentResponse.observe(viewLifecycleOwner) {

            it?.let {
                val directions = PostpaidBillDetailsRechargeFragmentDirections.actionRechargeSuccess(
                    successDth = it,
                    selectedOperator = postpaidBillDetailsFragmentVM.operatorResponse.get(),

                    )
                findNavController().navigate(directions)

                postpaidBillDetailsFragmentVM.paymentResponse.value = null
            }


        }

        postpaidBillDetailsFragmentVM.failedRecharge.observe(viewLifecycleOwner) {

            it?.let {

                postpaidBillDetailsFragmentVM.failedRecharge.value = null
                val directions = PostpaidBillDetailsRechargeFragmentDirections.actionRechargeSuccess(
                    successDth = null,
                    selectedOperator = postpaidBillDetailsFragmentVM.operatorResponse.get(),
                    amount = binding.amountEt.text.toString(),
                    mobile = postpaidBillDetailsFragmentVM.mobileNumber.value

                )
                findNavController().navigate(directions)


            }


        }


        postpaidBillDetailsFragmentVM.opertaorList.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)

        }


    }


}
