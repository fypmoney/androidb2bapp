package com.fypmoney.view.recharge.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.PaymentProcessingFragmentBinding
import com.fypmoney.extension.toVisible
import com.fypmoney.view.recharge.model.PayAndRechargeResponse
import com.fypmoney.view.recharge.viewmodel.PaymentProcessingFragmentVM


class PaymentProcessingFragment : BaseFragment<PaymentProcessingFragmentBinding, PaymentProcessingFragmentVM>() {

    private  val paymentProcessingFragmentVM by viewModels<PaymentProcessingFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: PaymentProcessingFragmentBinding
    private val args:PaymentProcessingFragmentArgs by navArgs()

    companion object {
        fun newInstance() = PaymentProcessingFragment()
    }

    override fun onTryAgainClicked() {
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int  = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int  = R.layout.payment_processing_fragment

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): PaymentProcessingFragmentVM = paymentProcessingFragmentVM

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentProcessingFragmentVM.rechargeRequest = args.payAndRechargeRequest
        paymentProcessingFragmentVM.billPaymentRequest = args.billPaymentRequest
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        setUpBinding()
        paymentProcessingFragmentVM.updateText()
        paymentProcessingFragmentVM.rechargeRequest?.let {
            paymentProcessingFragmentVM.callMobileRecharge(
                it
            )
        }
        paymentProcessingFragmentVM.billPaymentRequest?.let {
            paymentProcessingFragmentVM.payPostpaidBill()
        }
        paymentProcessingFragmentVM.updateText()
    }

    private fun setUpBinding() {
        paymentProcessingFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        paymentProcessingFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelEvent(it: PaymentProcessingFragmentVM.PaymentProcessingEvent?) {
        when(it){
            is PaymentProcessingFragmentVM.PaymentProcessingEvent.ShowPrepaidFailedScreen -> {
                val directions  = PaymentProcessingFragmentDirections.navigateFromPaymentProcessingToPaymentSuccess(
                    successResponse = null,
                    selectedOperator = null,
                    mobile = paymentProcessingFragmentVM.rechargeRequest?.cardNo,
                    amount = paymentProcessingFragmentVM.rechargeRequest?.amount.toString()

                )
                findNavController().navigate(directions)
            }
            is PaymentProcessingFragmentVM.PaymentProcessingEvent.ShowPostPaidFailedScreen -> {
                val directions  = PaymentProcessingFragmentDirections.navigateFromPaymentProcessingToPaymentSuccess(
                    successResponse = null,
                    selectedOperator = null,
                    mobile = paymentProcessingFragmentVM.billPaymentRequest?.cardNo,
                    amount = paymentProcessingFragmentVM.billPaymentRequest?.billAmount.toString()

                )
                findNavController().navigate(directions)
            }

            is PaymentProcessingFragmentVM.PaymentProcessingEvent.ShowPendingScreen -> {

            }
            is PaymentProcessingFragmentVM.PaymentProcessingEvent.ShowSuccessScreen -> {
                val directions  = PaymentProcessingFragmentDirections.navigateFromPaymentProcessingToPaymentSuccess(
                    successResponse = it.payAndRechargeResponse,
                    selectedOperator = null,
                    mobile = paymentProcessingFragmentVM.rechargeRequest?.cardNo,
                    amount = paymentProcessingFragmentVM.rechargeRequest?.amount.toString())
                findNavController().navigate(directions)

            }
            null -> TODO()
            is PaymentProcessingFragmentVM.PaymentProcessingEvent.ShowPostPaidSuccessScreen -> {
                val directions  = PaymentProcessingFragmentDirections.navigateFromPaymentProcessingToPaymentSuccess(
                    successResponse = PayAndRechargeResponse(isPurchased = it.billPaymentResponse.isPurchased),
                    selectedOperator = null,
                    mobile = paymentProcessingFragmentVM.billPaymentRequest?.cardNo,
                    amount = paymentProcessingFragmentVM.billPaymentRequest?.billAmount.toString()

                )
                findNavController().navigate(directions)
            }
        }
    }

    private fun handelState(it: PaymentProcessingFragmentVM.PaymentProcessingState?) {
        when(it){
            is PaymentProcessingFragmentVM.PaymentProcessingState.Error -> {
                binding.progressCpi.toVisible()
                binding.titleTv.text = it.errorResponseInfo.msg

            }
            PaymentProcessingFragmentVM.PaymentProcessingState.Loading -> {
                    binding.progressCpi.toVisible()



            }
            is PaymentProcessingFragmentVM.PaymentProcessingState.Success -> {

            }
            null -> TODO()
            is PaymentProcessingFragmentVM.PaymentProcessingState.BillSuccess -> {

            }
            is PaymentProcessingFragmentVM.PaymentProcessingState.UpdateTitle -> {
                binding.titleTv.text = it.title
            }
        }
    }
}