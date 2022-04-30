package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.SelectedPlanDetailsRechargeFragmentBinding
import com.fypmoney.view.recharge.viewmodel.SelectedPlanDetailsRechargeFragmentVM
import kotlinx.android.synthetic.main.toolbar.*


class SelectedPlanDetailsRechargeFragment :
    BaseFragment<SelectedPlanDetailsRechargeFragmentBinding, SelectedPlanDetailsRechargeFragmentVM>() {
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
            toolbarTitle = "Mobile Recharge"
        )

        setObserver()
        setBindings()


    }

    private fun setBindings() {
       /* mViewBinding.continueBtn.setOnClickListener {
            mViewModel.callMobileRecharge(
                mViewModel.selectedPlan.value,
                mViewModel.mobile.value,
                mViewModel.operatorResponse.value,
                mViewModel.planType.value
            )
        }*/
    }

    override fun onTryAgainClicked() {

    }


    private fun setObserver() {


        selectedPlanDetailsRechargeFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
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

    private fun handelEvent(it: SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeEvent?) {
        when(it){
            SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeEvent.ChangePlan -> {
                findNavController().navigateUp()
            }
            is SelectedPlanDetailsRechargeFragmentVM.SelectedPlanDetailsRechargeEvent.Pay ->{

            }
            null -> TODO()
        }
    }


}
