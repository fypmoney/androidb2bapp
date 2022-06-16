package com.fypmoney.view.giftcard

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentCreateEGiftCardBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddMoneyView
import com.fypmoney.view.fragment.TaskMessageInsuficientFuntBottomSheet
import com.fypmoney.view.giftcard.adapters.GiftCardPossibleDenominationAmountListAdapter
import com.fypmoney.view.giftcard.adapters.GiftCardPossibleDenominationAmountUiModel.Companion.possibleDenominationListToGiftCardPossibleDenominationAmountUiModel
import com.fypmoney.view.giftcard.model.GiftCardBrandDetails
import com.fypmoney.view.giftcard.viewModel.CreateEGiftCardFragmentVM
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import kotlinx.android.synthetic.main.toolbar_gift_card.view.*


class CreateEGiftCardFragment : BaseFragment<FragmentCreateEGiftCardBinding, CreateEGiftCardFragmentVM>() {
    private val createEGiftCardFragmentVM by viewModels<CreateEGiftCardFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentCreateEGiftCardBinding
    private val args:CreateEGiftCardFragmentArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_create_e_gift_card
    }

    override fun getViewModel(): CreateEGiftCardFragmentVM  = createEGiftCardFragmentVM

    override fun onTryAgainClicked() {
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        createEGiftCardFragmentVM.brandCode = args.productCode
        setUpBinding()
        setUpRecyclerView()
        setupObserver()
        createEGiftCardFragmentVM.getGiftCardBrandDetails()
    }

    private fun setUpBinding() {
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        tnc.setOnClickListener {

        }

        binding.giftAmount.doOnTextChanged { text, start, before, count ->
            if(text!!.isNotEmpty()){
               /* binding.giftAmount.setText(String.format(getString(R.string.amount_with_currency),text.toString()))
                createEGiftCardFragmentVM.amountSelected.value = text.toString().replace("₹ ","").toInt()
                */
                binding.payBtn.isEnabled = true
            }else{
                binding.payBtn.isEnabled = false
              /*  binding.giftAmount.setText("")
                createEGiftCardFragmentVM.amountSelected.value = 0
                createEGiftCardFragmentVM.amountSelected.value = text.toString().replace("₹ ","").toInt()
            */}
        }

    }

    private fun setUpRecyclerView() {
        binding.giftCardAmountRv.adapter = GiftCardPossibleDenominationAmountListAdapter(
            lifecycleOwner = viewLifecycleOwner,
            onAmountClicked = {
                createEGiftCardFragmentVM.amountSelected.value = it
            }
        )
    }
    private fun setupObserver(){
        createEGiftCardFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        createEGiftCardFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelState(it: CreateEGiftCardFragmentVM.CreateEGiftCardState?) {
        when(it){
            CreateEGiftCardFragmentVM.CreateEGiftCardState.BalanceError -> {
                binding.payBtn.setBusy(false)
                binding.payBtn.isEnabled = true
            }
            is CreateEGiftCardFragmentVM.CreateEGiftCardState.BrandDetailsSuccess -> {
                setupBrandDetails(it.details)
            }
            CreateEGiftCardFragmentVM.CreateEGiftCardState.Error -> {

            }
            CreateEGiftCardFragmentVM.CreateEGiftCardState.Loading ->{
                binding.payBtn.setBusy(true)
            }
            is CreateEGiftCardFragmentVM.CreateEGiftCardState.Success ->{}
            is CreateEGiftCardFragmentVM.CreateEGiftCardState.ValidationError ->{}
            is CreateEGiftCardFragmentVM.CreateEGiftCardState.PossibleDenominationList -> {
                if(it.list!!.isNotEmpty()){
                    binding.giftCardAmountRv.toVisible()
                    (binding.giftCardAmountRv.adapter as GiftCardPossibleDenominationAmountListAdapter).
                    submitList(it.list.map {
                        possibleDenominationListToGiftCardPossibleDenominationAmountUiModel(context = requireContext(),
                            amount = it)
                    })
                }else{
                    binding.giftCardAmountRv.toGone()
                }
            }
            null ->{}

        }
    }

    private fun setupBrandDetails(details: GiftCardBrandDetails?) {
        toolbar.toolbar_title.text = details?.displayName
        details?.detailImage?.let {
            Utility.setImageUsingGlideWithShimmerPlaceholder(imageView = binding.giftCardIv, url = it)
        }
        binding.maxMinAmountTv.text = String.format(getString(R.string.min__max_gift_price),
            Utility.convertToRs(details?.minPrice.toString()),Utility.convertToRs(details?.maxPrice.toString()))
    }


    private fun handelEvent(it: CreateEGiftCardFragmentVM.CreateEGiftCardEvent?) {
        when(it){
            CreateEGiftCardFragmentVM.CreateEGiftCardEvent.OnPayClickedEvent -> {

            }
            CreateEGiftCardFragmentVM.CreateEGiftCardEvent.OnSomeOneClickedEvent ->{

            }
            is CreateEGiftCardFragmentVM.CreateEGiftCardEvent.ShowLowBalanceAlert ->{
                callInsufficientFundMessageSheet(it.amount)
            }
            CreateEGiftCardFragmentVM.CreateEGiftCardEvent.ShowPaymentProcessingScreen ->{

            }
            null ->{}
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
