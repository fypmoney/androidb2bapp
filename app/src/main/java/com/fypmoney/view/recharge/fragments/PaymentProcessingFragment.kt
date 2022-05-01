package com.fypmoney.view.recharge.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.PaymentProcessingFragmentBinding
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import kotlinx.coroutines.delay


class PaymentProcessingFragment : BaseFragment<PaymentProcessingFragmentBinding,PaymentProcessingFragmentVM>() {

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
    override fun getViewModel(): PaymentProcessingFragmentVM  = paymentProcessingFragmentVM

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentProcessingFragmentVM.rechargeRequest = args.payAndRechargeRequest
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        setUpBinding()
        paymentProcessingFragmentVM.rechargeRequest?.let {
            paymentProcessingFragmentVM.callMobileRecharge(
                it
            )
        }
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
            is PaymentProcessingFragmentVM.PaymentProcessingEvent.ShowFailedScreen -> {

            }
            is PaymentProcessingFragmentVM.PaymentProcessingEvent.ShowPendingScreen -> {

            }
            is PaymentProcessingFragmentVM.PaymentProcessingEvent.ShowSuccessScreen -> {
                val directions  = PaymentProcessingFragmentDirections.navigateFromPaymentProcessingToPaymentSuccess(
                    successResponse = it.payAndRechargeResponse,
                    selectedOperator = null,
                    mobile = paymentProcessingFragmentVM.rechargeRequest?.cardNo,
                    amount = paymentProcessingFragmentVM.rechargeRequest?.amount.toString()

                )
                findNavController().navigate(directions)

            }
            null -> TODO()
        }
    }

    private fun handelState(it: PaymentProcessingFragmentVM.PaymentProcessingState?) {
        when(it){
            is PaymentProcessingFragmentVM.PaymentProcessingState.Error -> {
                binding.progressCpi.toVisible()
                binding.titleTv.text = it.errorResponseInfo.msg
                /*lifecycleScope.launchWhenResumed {
                    delay(5000)
                    findNavController().navigateUp()
                }*/
                val directions  = PaymentProcessingFragmentDirections.navigateFromPaymentProcessingToPaymentSuccess(
                    successResponse = null,
                    selectedOperator = null,
                    mobile = paymentProcessingFragmentVM.rechargeRequest?.cardNo,
                    amount = paymentProcessingFragmentVM.rechargeRequest?.amount.toString()

                )
                findNavController().navigate(directions)
            }
            PaymentProcessingFragmentVM.PaymentProcessingState.Loading -> {
                binding.progressCpi.toVisible()

                    lifecycleScope.launchWhenResumed {
                        binding.titleTv.text = paymentProcessingFragmentVM.rechargeRequest?.let { it1 ->
                            String.format(getString(R.string.processing_payment_of),
                                Utility.convertToRs(it1.amount.toString()))
                        }
                        delay(1000)
                        binding.titleTv.text = paymentProcessingFragmentVM.rechargeRequest?.let { it1 ->
                            String.format(getString(R.string.processing_recharge_of), it1.cardNo.toString())
                        }
                    }


            }
            is PaymentProcessingFragmentVM.PaymentProcessingState.Success -> {

            }
            null -> TODO()
        }
    }
}