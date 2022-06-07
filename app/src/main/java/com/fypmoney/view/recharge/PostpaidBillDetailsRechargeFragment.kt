package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.connectivity.ApiConstant.API_FETCH_BILL
import com.fypmoney.connectivity.ApiConstant.API_GET_WALLET_BALANCE
import com.fypmoney.databinding.PostpaidBillDetailsRechargeFragmentBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddMoneyView
import com.fypmoney.view.fragment.TaskMessageInsuficientFuntBottomSheet
import com.fypmoney.view.interfaces.AcceptRejectClickListener
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

        args.selectedCircle?.let {
            postpaidBillDetailsFragmentVM.circleGot = it
        }?: kotlin.run {
            Utility.showToast(getString(R.string.please_try_again_after_some_time))
            findNavController().navigateUp()
        }
        args.rechargeType?.let {
            postpaidBillDetailsFragmentVM.rechargeType = it
        }?: kotlin.run {
            Utility.showToast(getString(R.string.please_try_again_after_some_time))
            findNavController().navigateUp()
        }
        args.mobile?.let {
            postpaidBillDetailsFragmentVM.mobileNumber = it
            binding.mobileNumberTv.text = it
        }
        args.selectedOperator?.let {
            postpaidBillDetailsFragmentVM.operatorResponse = it
            val circle = postpaidBillDetailsFragmentVM.circleGot ?: kotlin.run {
                ""
            }
            when (it.name) {
                "Airtel" -> {
                    binding.operatorCircleIv.setBackgroundResource(R.drawable.ic_airtel)
                    binding.operatorAndCircleTv.text = it.name?.let { it1->Utility.toTitleCase(postpaidBillDetailsFragmentVM.rechargeType)+","+it1+" "+circle } ?:
                    kotlin.run { Utility.toTitleCase(postpaidBillDetailsFragmentVM.rechargeType)+","+it.name }
                }
                "Vodafone" -> {
                    binding.operatorCircleIv.setBackgroundResource(R.drawable.ic_vodafone)
                    binding.operatorAndCircleTv.text = it.name?.let { it1->Utility.toTitleCase(postpaidBillDetailsFragmentVM.rechargeType)+","+it1+" "+circle } ?:
                    kotlin.run { Utility.toTitleCase(postpaidBillDetailsFragmentVM.rechargeType)+","+it.name }
                }
                "JIO" -> {
                    binding.operatorCircleIv.setBackgroundResource(R.drawable.ic_jio)
                    binding.operatorAndCircleTv.text = it.name?.let { it1->Utility.toTitleCase(postpaidBillDetailsFragmentVM.rechargeType)+","+it1+" "+circle } ?:
                    kotlin.run { Utility.toTitleCase(postpaidBillDetailsFragmentVM.rechargeType)+","+it.name }
                }
                else -> {
                    binding.operatorCircleIv.setBackgroundResource(R.drawable.ic_user2)
                    binding.operatorAndCircleTv.text = it.name?.let { it1->Utility.toTitleCase(postpaidBillDetailsFragmentVM.rechargeType)+","+it1+"-"+circle } ?: kotlin.run { Utility.toTitleCase(postpaidBillDetailsFragmentVM.rechargeType)+","+it.name }


                }
            }

        }?: kotlin.run {
            Utility.showToast(getString(R.string.please_try_again_after_some_time))
            findNavController().navigateUp()
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
        postpaidBillDetailsFragmentVM.callFetchBillsInformation(postpaidBillDetailsFragmentVM.mobileNumber!!, postpaidBillDetailsFragmentVM.operatorResponse!!.operatorId!!)


    }

    private fun setBindings() {
        binding.amountEt.doOnTextChanged { text, start, before, count ->
            if(!text.isNullOrEmpty()){
                binding.continueBtn.isEnabled = true
                postpaidBillDetailsFragmentVM.amount = text.toString()
            }else{
                binding.continueBtn.isEnabled = false
            }
        }
    }

    override fun onTryAgainClicked() {

    }


    private fun setObserver() {
        postpaidBillDetailsFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        postpaidBillDetailsFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }


    }

    private fun handelEvent(it: PostpaidBillDetailsFragmentVM.PostpaidBilDetailsEvent?) {
        when(it){
            is PostpaidBillDetailsFragmentVM.PostpaidBilDetailsEvent.ShowLowBalanceAlert -> {
                callInsufficientFundMessageSheet(it.amount)

            }
            is PostpaidBillDetailsFragmentVM.PostpaidBilDetailsEvent.ShowPaymentProcessingScreen -> {
                val directions = PostpaidBillDetailsRechargeFragmentDirections.actionPostpaidBillToPaymentProcessing(it.billPaymentRequest)
                findNavController().navigate(directions)
            }
            null -> TODO()
            PostpaidBillDetailsFragmentVM.PostpaidBilDetailsEvent.OnPayClickEvent -> {
                askForDevicePassword()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        postpaidBillDetailsFragmentVM.fetchBalance()
                    }

                }
            }
        }
    }
    private fun handelState(it: PostpaidBillDetailsFragmentVM.PostpaidBillDetailsState?) {
        when(it){
            is PostpaidBillDetailsFragmentVM.PostpaidBillDetailsState.Error -> {
                when(it.api){
                    API_FETCH_BILL->{
                        binding.billErrorTv.toVisible()
                    }
                    API_GET_WALLET_BALANCE->{
                        binding.continueBtn.setBusy(false)
                    }
                }
            }
            is PostpaidBillDetailsFragmentVM.PostpaidBillDetailsState.FetchBillSuccess -> {
                binding.billErrorTv.toGone()
                if(it.bill.status!!){
                    it.bill.duedate?.let {
                        binding.billDueDateTv.text = String.format(getString(R.string.bill_due_date),it)

                    }
                    it.bill.amount?.let{
                        binding.billDueAmountTv.text = getString(R.string.Rs)+ it
                        binding.amountEt.setText(it)
                    }
                }else{
                    //Utility.showToast(it.bill.message)
                    //findNavController().navigateUp()
                    binding.billErrorTv.toVisible()
                }


            }
            PostpaidBillDetailsFragmentVM.PostpaidBillDetailsState.Loading -> {
                binding.billErrorTv.toGone()
                binding.continueBtn.setBusy(true)
            }
            null -> TODO()
            is PostpaidBillDetailsFragmentVM.PostpaidBillDetailsState.BalanceSuccess -> {
                binding.continueBtn.setBusy(false)
            }
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
