package com.fypmoney.view.giftcard.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentCreateEGiftCardBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.YES
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddMoneyView
import com.fypmoney.view.fragment.TaskMessageInsuficientFuntBottomSheet
import com.fypmoney.view.giftcard.adapters.GiftCardPossibleDenominationAmountListAdapter
import com.fypmoney.view.giftcard.adapters.GiftCardPossibleDenominationAmountUiModel.Companion.possibleDenominationListToGiftCardPossibleDenominationAmountUiModel
import com.fypmoney.view.giftcard.model.GiftCardBrandDetails
import com.fypmoney.view.giftcard.viewModel.CreateEGiftCardFragmentVM
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import kotlinx.android.synthetic.main.toolbar_gift_card.view.*

private const val PICK_CONTACT = 1
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

        binding.giftAmount.doOnTextChanged { text, start, before, count ->
            if(text!!.isNotEmpty()){
                binding.giftAmount.error = null
                binding.payBtn.isEnabled = true
                createEGiftCardFragmentVM.createEGiftCardModel.amount = text.toString().toLong()
            }else{
                binding.payBtn.isEnabled = false
              }
        }
        binding.phoneEt.doOnTextChanged{text, start, before, count ->
            if(text!!.isNotEmpty()){
                binding.phoneTil.error = null
                createEGiftCardFragmentVM.createEGiftCardModel.destinationMobileNo = text.toString()
            }
        }
        binding.nameEt.doOnTextChanged{text, start, before, count ->
            if(text!!.isNotEmpty()){
                binding.nameTil.error = null
                createEGiftCardFragmentVM.createEGiftCardModel.destinationName = text.toString()
            }
        }
        binding.emailEt.doOnTextChanged{text, start, before, count ->
            if(text!!.isNotEmpty()){
                createEGiftCardFragmentVM.createEGiftCardModel.destinationEmail = text.toString()
            }
        }

    }

    private fun setUpRecyclerView() {
        binding.giftCardAmountRv.adapter = GiftCardPossibleDenominationAmountListAdapter(
            lifecycleOwner = viewLifecycleOwner,
            onAmountClicked = {
                binding.giftAmount.setText(it.toString())
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
                binding.payBtn.setBusy(false)
            }
            CreateEGiftCardFragmentVM.CreateEGiftCardState.Loading ->{
                binding.payBtn.setBusy(true)
            }
            is CreateEGiftCardFragmentVM.CreateEGiftCardState.Success ->{
                binding.payBtn.setBusy(false)
            }
            is CreateEGiftCardFragmentVM.CreateEGiftCardState.ValidationError ->{
                binding.payBtn.setBusy(false)
                handelValidationError(it.validationError)
            }
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
            CreateEGiftCardFragmentVM.CreateEGiftCardState.MySelfClickedState -> {
                if(binding.myselfRb.isChecked){
                    createEGiftCardFragmentVM.giftCardForWhom = CreateEGiftCardFragmentVM.GiftCardForWhom.MySelf
                    binding.someoneCl.toGone()
                }else{
                    createEGiftCardFragmentVM.giftCardForWhom = CreateEGiftCardFragmentVM.GiftCardForWhom.Someone
                    binding.someoneCl.toVisible()
                }
            }
            CreateEGiftCardFragmentVM.CreateEGiftCardState.SomeOneClickedState -> {
                if(binding.someoneRb.isChecked){
                    createEGiftCardFragmentVM.giftCardForWhom = CreateEGiftCardFragmentVM.GiftCardForWhom.Someone
                    binding.someoneCl.toVisible()
                }else{
                    createEGiftCardFragmentVM.giftCardForWhom = CreateEGiftCardFragmentVM.GiftCardForWhom.MySelf
                    binding.someoneCl.toGone()
                }
            }
            null ->{

            }
        }
    }

    private fun handelValidationError(validationError: CreateEGiftCardFragmentVM.ValidationErrorData) {
        when(validationError.field){
            CreateEGiftCardFragmentVM.Field.Amount -> {
                binding.giftAmount.error = validationError.validationMsg
            }
            CreateEGiftCardFragmentVM.Field.MobileNumber -> {
                binding.phoneTil.error = validationError.validationMsg
            }
            CreateEGiftCardFragmentVM.Field.Name -> {
                binding.nameTil.error = validationError.validationMsg
            }
        }
    }

    private fun setupBrandDetails(details: GiftCardBrandDetails?) {
        toolbar.toolbar_title.text = details?.displayName
        details?.detailImage?.let {
            Utility.setImageUsingGlideWithShimmerPlaceholder(imageView = binding.giftCardIv, url = it)
        }
        binding.maxMinAmountTv.text = String.format(getString(R.string.min__max_gift_price),
            Utility.convertToRs(details?.minPrice.toString()),Utility.convertToRs(details?.maxPrice.toString()))
        details?.fixedDenomiation?.let {
            binding.giftAmount.isEnabled = it != YES

        }
        tnc.setOnClickListener {
            val intent = Intent(requireContext(), ExploreInAppWebview::class.java)
            intent.putExtra(AppConstants.IN_APP_URL, details?.tncLink)
            startActivity(intent)
        }
    }


    private fun handelEvent(it: CreateEGiftCardFragmentVM.CreateEGiftCardEvent?) {
        when(it){
            CreateEGiftCardFragmentVM.CreateEGiftCardEvent.OnPayClickedEvent -> {

            }
            is CreateEGiftCardFragmentVM.CreateEGiftCardEvent.ShowLowBalanceAlert ->{
                callInsufficientFundMessageSheet(it.amount)
            }
            null ->{}
            CreateEGiftCardFragmentVM.CreateEGiftCardEvent.OnSelectFromContactEvent -> {
                selectContactFromPhoneContactList(PICK_CONTACT)
            }
            is CreateEGiftCardFragmentVM.CreateEGiftCardEvent.ShowPaymentProcessingScreen -> {
                findNavController().navigate(CreateEGiftCardFragmentDirections.actionCreateGiftCardToPaymentProcessing(it.createEGiftCardModel))
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

    private fun selectContactFromPhoneContactList(requestCode:Int){
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PICK_CONTACT ->{
                if(resultCode== Activity.RESULT_OK){
                    when(val result = Utility.getPhoneNumberFromContact(requireActivity(), data)){
                        is Utility.MobileNumberFromPhoneBook.MobileNumberFound -> {
                            lifecycleScope.launchWhenResumed {
                                binding.phoneEt.setText(result.phoneNumber)
                                result.name?.let {
                                    binding.nameEt.setText(it)
                                }
                            }
                        }
                        is Utility.MobileNumberFromPhoneBook.UnableToFindMobileNumber -> {
                            Utility.showToast(result.errorMsg)
                        }
                    }
                }
            }
        }


    }

}
