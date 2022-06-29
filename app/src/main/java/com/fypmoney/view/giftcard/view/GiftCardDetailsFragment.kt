package com.fypmoney.view.giftcard.view

import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentGiftCardDetailsBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.YES
import com.fypmoney.util.GIFT_CARD_ID_NOT_FOUND
import com.fypmoney.util.Utility
import com.fypmoney.util.Utility.copyTextToClipBoard
import com.fypmoney.util.Utility.shareScreenShotContent
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.giftcard.model.GiftCardDetail
import com.fypmoney.view.giftcard.viewModel.GiftCardDetailsFragmentVM
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*

class GiftCardDetailsFragment : BaseFragment<FragmentGiftCardDetailsBinding, GiftCardDetailsFragmentVM>() {

    companion object {
        fun newInstance() = GiftCardDetailsFragment()
    }

    private val giftCardDetailsFragmentVM  by viewModels<GiftCardDetailsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentGiftCardDetailsBinding
    private val navArgs:GiftCardDetailsFragmentArgs by navArgs()
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
    override fun getLayoutId(): Int  = R.layout.fragment_gift_card_details

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): GiftCardDetailsFragmentVM = giftCardDetailsFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        navArgs.giftCardId?.let {
            giftCardDetailsFragmentVM.giftCardId = it
        }?: kotlin.run {
            FirebaseCrashlytics.getInstance().recordException(Throwable(GIFT_CARD_ID_NOT_FOUND))
            Utility.showToast(getString(R.string.please_try_again_after_some_time))
            findNavController().navigate(GiftCardDetailsFragmentDirections.actionGiftCardDetailsToHome())
        }
        setUpViews()
        setupObserver()
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        giftCardDetailsFragmentVM.getGiftCardDetails()
    }
    private fun setUpViews() {
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = getString(R.string.gift_card_details)
        )
        binding.layout.toolbar_image.setImageResource(R.drawable.ic_share_white)
        binding.layout.toolbar_image.toVisible()
        binding.layout.toolbar_image.setOnClickListener {
            giftCardDetailsFragmentVM.onShareClick()
        }
    }

    private fun setupObserver() {
        giftCardDetailsFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        giftCardDetailsFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelState(it: GiftCardDetailsFragmentVM.GiftCardDetailsState?) {
        when(it){
            GiftCardDetailsFragmentVM.GiftCardDetailsState.Loading -> {

            }
            is GiftCardDetailsFragmentVM.GiftCardDetailsState.Success -> {
                showGiftCardDetails(it.giftCardDetails)
            }
            is GiftCardDetailsFragmentVM.GiftCardDetailsState.Error -> {
                FirebaseCrashlytics.getInstance().recordException(Throwable(it.errorResponseInfo.errorCode))
                Utility.showToast(it.errorResponseInfo.msg)
                findNavController().navigate(GiftCardDetailsFragmentDirections.actionGiftCardDetailsToHome())

            }
            null -> {

            }
        }
    }

    private fun showGiftCardDetails(giftCardDetails: GiftCardDetail) {
        with(giftCardDetails){
            binding.layout.toolbar_title.text = brandName
            Utility.setImageUsingGlideWithShimmerPlaceholder(imageView = binding.giftCardBrandLogoIv, url = brandLogo)
            binding.giftCardAmountTv.text = String.format(getString(R.string.amount_with_currency),Utility.convertToRs(amount))
            voucherNo?.let {
                binding.giftCardVoucherNumberValueTv.text = it
            }?: kotlin.run {
                binding.voucherNumberCl.toGone()
            }
            voucherPin?.let {
                binding.giftCardVoucherPinValueTv.text = it
            }?: kotlin.run {
                binding.voucherPinCl.toGone()
            }
            endDate?.let {
                binding.voucherValidityValueTv.text = Utility.parseDateTime(
                    it,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT9
                )
            }?: kotlin.run {
                binding.voucherValidtyCl.toGone()
            }
            fypOrderNo?.let {
                binding.fypOrderNoValueTv.text = it
            }?: kotlin.run {
                binding.fypOrderNoCl.toGone()
            }
            myntsRewarded?.let {
                if(isGifted.equals(YES)){
                    val loggedInUserMobileNo = Utility.getCustomerDataFromPreference()?.mobile!!
                    if(loggedInUserMobileNo.equals(destinationMobileNo)){
                        binding.reccivedMyntsTv.toGone()
                        binding.playNowBtn.toGone()
                    }else{
                        binding.reccivedMyntsTv.toVisible()
                        binding.playNowBtn.toGone()
                        binding.reccivedMyntsTv.text = String.format(getString(R.string.you_have_won_mynts1),it)
                    }
                }else{
                    binding.reccivedMyntsTv.toVisible()
                    binding.playNowBtn.toGone()
                    binding.reccivedMyntsTv.text = String.format(getString(R.string.you_have_won_mynts1),it)
                }
            }?: kotlin.run {
                binding.reccivedMyntsTv.toGone()
                binding.playNowBtn.toGone()
            }
        }
    }

    private fun handelEvent(it: GiftCardDetailsFragmentVM.GiftCardDetailsEvent?) {
        when(it){
            GiftCardDetailsFragmentVM.GiftCardDetailsEvent.NavigateToArcade -> {
                findNavController().navigate(GiftCardDetailsFragmentDirections.actionGiftCardDetailsToRewards())
            }
            GiftCardDetailsFragmentVM.GiftCardDetailsEvent.NavigateToHomeScreen -> {
                findNavController().navigate(GiftCardDetailsFragmentDirections.actionGiftCardDetailsToHome())
            }
            is GiftCardDetailsFragmentVM.GiftCardDetailsEvent.RedeemNow -> {
                showRedeemSteps(it.redeemNowTxt,it.redeemLink)
            }
            null -> {

            }
            is GiftCardDetailsFragmentVM.GiftCardDetailsEvent.RedeemNowWithoutSteps -> {
                openWebViewWithFypCard(it.redeemLink)
            }
            is GiftCardDetailsFragmentVM.GiftCardDetailsEvent.ShareGiftCardDetails -> {
                binding.layout.toGone()
                binding.playNowBtn.toGone()
                binding.reccivedMyntsTv.toGone()
                binding.reedemBtn.toGone()
                Utility.takeScreenShot(binding.root)?.let { it1 -> context?.let { it2 ->
                    shareScreenShotContent(it1,
                        it2,
                        "Dear User," +
                                "\n" +
                                "\n" +
                                "\n" +
                                "Woohoo! Here's your ${it.giftCardDetails.brandName} worth INR ${Utility.convertToRs(it.giftCardDetails.amount)} purchased via Fyp, India's leading neobank for teens and families. Here are the details-" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "Voucher Number: ${it.giftCardDetails.voucherNo}" +
                                "\n" +
                                "Voucher PIN: ${it.giftCardDetails.voucherPin}" +
                                "\n" +
                                "Validity: ${Utility.parseDateTime(
                                    it.giftCardDetails.endDate,
                                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT9
                                )}" +
                                "\n" +
                                "Terms and Conditions: ${it.giftCardDetails.tncLink}" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "Join 1 Mn+ Fyp users and earn upto 10X rewards on purchase of Gift Vouchers- https://fypmoney.in/app"
                    )
                } }
                binding.layout.toVisible()
                binding.playNowBtn.toGone()
                if(giftCardDetailsFragmentVM.giftCardDetails.isGifted.equals(YES)){
                    val loggedInUserMobileNo = Utility.getCustomerDataFromPreference()?.mobile!!
                    if(loggedInUserMobileNo.equals(giftCardDetailsFragmentVM.giftCardDetails.destinationMobileNo)){
                        binding.reccivedMyntsTv.toGone()
                    }else{
                        binding.reccivedMyntsTv.toVisible()
                    }
                }else{
                    binding.reccivedMyntsTv.toVisible()
                }

                binding.reedemBtn.toVisible()
            }
            GiftCardDetailsFragmentVM.GiftCardDetailsEvent.VoucherValueCopyEvent -> {
                copyTextToClipBoard(requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager,
                    binding.giftCardVoucherNumberValueTv.text.toString()
                    )
            }
            GiftCardDetailsFragmentVM.GiftCardDetailsEvent.VoucherValuePinEvent -> {
                copyTextToClipBoard(requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager,
                    binding.giftCardVoucherPinValueTv.text.toString()
                )
            }
        }
    }

    private fun showRedeemSteps(redeemNowTxt: String,redeemNowLink: String) {
        val howToRedeemGiftCardBottomSheet = HowToRedeemGiftCardBottomSheet(
            redeemTxt = redeemNowTxt,
            redeemLink = redeemNowLink,
            onRedeemNowClick = {
                openWebViewWithFypCard(it)
            })
        howToRedeemGiftCardBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        howToRedeemGiftCardBottomSheet.show(childFragmentManager, "HowToRedeemSheet")
    }

    fun openWebViewWithFypCard(redeemNowLink:String){
        val intent = Intent(requireContext(), StoreWebpageOpener2::class.java)
        intent.putExtra(ARG_WEB_URL_TO_OPEN, redeemNowLink)
        startActivity(intent)
    }


}