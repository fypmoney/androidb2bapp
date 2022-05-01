package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.SelectedPlanDetailsRechargeFragmentBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddMoneyView
import com.fypmoney.view.fragment.TaskMessageInsuficientFuntBottomSheet
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.fypmoney.view.recharge.viewmodel.SelectedPlanDetailsRechargeFragmentVM
import kotlinx.android.synthetic.main.toolbar.*


class SelectedPlanDetailsRechargeFragment: BaseFragment<SelectedPlanDetailsRechargeFragmentBinding, SelectedPlanDetailsRechargeFragmentVM>() {

    private  val selectedPlanDetailsRechargeFragmentVM by viewModels<SelectedPlanDetailsRechargeFragmentVM> { defaultViewModelProviderFactory }

    private lateinit var binding: SelectedPlanDetailsRechargeFragmentBinding

    private val args: SelectedPlanDetailsRechargeFragmentArgs by navArgs()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.selected_plan_details_recharge_fragment
    }

    override fun getViewModel(): SelectedPlanDetailsRechargeFragmentVM {
        return selectedPlanDetailsRechargeFragmentVM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding = getViewDataBinding()


        selectedPlanDetailsRechargeFragmentVM.selectedPlan = args.selectedPlan

        selectedPlanDetailsRechargeFragmentVM.operatorResponse = args.selectedOperator
        selectedPlanDetailsRechargeFragmentVM.mobile = args.mobile
        selectedPlanDetailsRechargeFragmentVM.planType = args.planType
        selectedPlanDetailsRechargeFragmentVM.rechargeType = args.rechargeType

        binding.amountTv.text = getString(R.string.Rs) + args.selectedPlan?.rs
        binding.details.text = args.selectedPlan?.desc

        binding.planType.text = args.planType

        binding.tvUserNumber.text = args.mobile

        when (selectedPlanDetailsRechargeFragmentVM.operatorResponse?.name) {
            "Airtel" -> {
                binding.operatorIv.setBackgroundResource(R.drawable.ic_airtel)
            }
            "Vodafone" -> {
                binding.operatorIv.setBackgroundResource(R.drawable.ic_vodafone)
            }
            "JIO" -> {
                binding.operatorIv.setBackgroundResource(R.drawable.ic_jio)
            }
        }
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = selectedPlanDetailsRechargeFragmentVM.operatorResponse?.name+" "+ Utility.toTitleCase(selectedPlanDetailsRechargeFragmentVM.rechargeType)
        )

        setObserver()


    }


    override fun onTryAgainClicked() {

    }


    private fun setObserver() {


        selectedPlanDetailsRechargeFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
        selectedPlanDetailsRechargeFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
      /*  mViewModel.success.observe(viewLifecycleOwner) {

            it?.let {

                mViewModel.success.postValue(null)


                val directions = SelectedPlanDetailsRechargeFragmentDirections.actionRechargeSuccess(
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
                    val directions = SelectedPlanDetailsRechargeFragmentDirections.actionRechargeSuccess(
                        successResponse = null,
                        selectedOperator = mViewModel.operatorResponse.value,

                        )
                    findNavController().navigate(directions)

                }


            }
        }
*/

    }

    private fun handelState(it: SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeState?) {
        when(it){
            is SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeState.Error -> {
                binding.continueBtn.setBusy(false)

            }
            SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeState.Loading -> {
                binding.continueBtn.setBusy(true)
            }
            is SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeState.Success -> {
                binding.continueBtn.setBusy(false)

            }
            null -> TODO()
        }
    }

    private fun handelEvent(it: SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeEvent?) {
        when(it){
            SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeEvent.ChangePlan -> {
                findNavController().navigateUp()
            }
            is SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeEvent.ShowLowBalanceAlert -> {
                callInsufficientFundMessageSheet(it.amount)
            }
            is SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeEvent.ShowPaymentProcessingScreen -> {
                val direction = SelectedPlanDetailsRechargeFragmentDirections.actionSelectedPlanDetailsToPaymentProcessing(
                    payAndRechargeRequest = it.payRequest
                )
                findNavController().navigate(direction)
            }
            null -> TODO()

        }
    }

    private fun callInsufficientFundMessageSheet(amount: String?) {
        var bottomSheetInsufficient: TaskMessageInsuficientFuntBottomSheet? = null
        val itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int, str: String) {
                bottomSheetInsufficient?.dismiss()
            }

            override fun onRejectClicked(pos: Int) {
                bottomSheetInsufficient?.dismiss()
                val intent = Intent(requireActivity(), AddMoneyView::class.java)
                intent.putExtra("amountshouldbeadded", Utility.convertToRs(amount))
                startActivity(intent)
            }

            override fun ondimiss() {
                bottomSheetInsufficient?.dismiss()
            }
        }
        bottomSheetInsufficient = TaskMessageInsuficientFuntBottomSheet(itemClickListener2,
            title = resources.getString(R.string.insufficient_bank_balance),
            subTitle =  resources.getString(R.string.insufficient_bank_body),
            amount = resources.getString(R.string.add_money_title1)+resources.getString(R.string.Rs)+Utility.convertToRs(amount),
            background = "#2d3039",
            titleColor  = "#ffffff",
            moneyTextColor  = "#ffffff",
            buttonColor  = "#8ECC9A",
        )

        bottomSheetInsufficient.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetInsufficient.show(requireActivity().supportFragmentManager, "Insufficient Fund")
    }


}
