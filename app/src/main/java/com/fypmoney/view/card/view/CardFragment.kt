package com.fypmoney.view.card.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.databinding.FragmentCardBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility.onCopyClicked
import com.fypmoney.util.Utility.stringToCardNumber
import com.fypmoney.view.activity.BankTransactionHistoryView
import com.fypmoney.view.activity.SetPinView
import com.fypmoney.view.card.adapter.CardScreenOptionsAdapter
import com.fypmoney.view.card.viewmodel.CardFragmentVM
import com.fypmoney.view.fragment.*
import com.fypmoney.view.ordercard.OrderCardView
import com.fypmoney.view.ordercard.trackorder.TrackOrderView
import com.fypmoney.view.setpindialog.SetPinDialogFragment
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.fypmoney.view.webview.WebViewActivity
import kotlinx.android.synthetic.main.toolbar.*

class CardFragment : BaseFragment<FragmentCardBinding, CardFragmentVM>() {

    private val cardFragmentVM by viewModels<CardFragmentVM> { defaultViewModelProviderFactory }

    private lateinit var _binding: FragmentCardBinding

    private val binding get() = _binding

    companion object {
        fun newInstance() = CardFragment()
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.fragment_card


    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): CardFragmentVM = cardFragmentVM

    override fun onTryAgainClicked() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        );
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.card_details_title),
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        setupRecyclerView()
        setUpObserver()
        cardFragmentVM.prepareInitialCardOption()
    }

    override fun onStart() {
        super.onStart()
        cardFragmentVM.callGetBankProfileApi()
    }

    private fun setupRecyclerView() {
        with(binding.cardOptionRv) {
            adapter = CardScreenOptionsAdapter(viewLifecycleOwner,
                onCardOptionClicked = {
                    cardFragmentVM.onCardOptionClicked(it)
                })
        }
    }

    private fun setUpObserver() {
        cardFragmentVM.state.observe(viewLifecycleOwner, {
            handleState(it)
        })
        cardFragmentVM.event.observe(viewLifecycleOwner, {
            handleEvents(it)
        })
    }

    private fun handleState(cardState: CardFragmentVM.CardState) {
        when (cardState) {
            is CardFragmentVM.CardState.BankProfilePopulate -> {

            }
            is CardFragmentVM.CardState.Error -> {
                when (cardState.purpose) {
                    ApiConstant.API_GET_VIRTUAL_CARD_REQUEST, ApiConstant.API_FETCH_VIRTUAL_CARD_DETAILS -> {
                        binding.errorCardDetailsCl.toVisible()
                        binding.cardCl.toGone()
                    }
                }
            }
            CardFragmentVM.CardState.Loading -> {

            }
            is CardFragmentVM.CardState.CardOptionState -> {
                (binding.cardOptionRv.adapter as CardScreenOptionsAdapter).submitList(cardState.listOfOption)
            }
            is CardFragmentVM.CardState.SetPinSuccessState -> {
                cardState.setPinResponseDetails.url.let {
                    intentToActivity(
                        SetPinView::class.java,
                        type = AppConstants.SET_PIN_URL,
                        url = it
                    )
                }
            }
            is CardFragmentVM.CardState.VirtualCardDetails -> {
                binding.errorCardDetailsCl.toGone()
                binding.cardCl.toVisible()
                binding.cardNoValueTv.text =
                    stringToCardNumber(cardState.virtualCardDetails.card_number)
                binding.cardVaildThrValueTv.text =
                    cardState.virtualCardDetails.expiry_month + "/" + cardState.virtualCardDetails.expiry_year
            }
        }
    }

    private fun handleEvents(cardEvent: CardFragmentVM.CardEvent) {
        when (cardEvent) {
            is CardFragmentVM.CardEvent.OnCVVClicked -> {
                if (cardEvent.isCvvVisible) {
                    binding.cardCvvValueTv.text = cardEvent.cvvValue
                    binding.viewCvvIv.setImageResource(R.drawable.ic_eye)
                } else {
                    binding.cardCvvValueTv.text = getString(R.string.three_star)
                    binding.viewCvvIv.setImageResource(R.drawable.ic_icon_feather_eye_off)

                }
            }
            CardFragmentVM.CardEvent.AccountStatementEvent -> {
                intentToActivity(BankTransactionHistoryView::class.java)
            }
            CardFragmentVM.CardEvent.CardSettingsEvent -> {
                callCardSettingsBottomSheet()
            }
            CardFragmentVM.CardEvent.OrderCardEvent -> {
                intentToActivity(OrderCardView::class.java)
            }
            CardFragmentVM.CardEvent.TrackOrderEvent -> {
                intentToActivity(TrackOrderView::class.java)

            }
            is CardFragmentVM.CardEvent.BlockUnblockEvent -> {
                val bottomSheet =
                    BlockUnblockCardBottomSheet(
                        cardEvent.bankProfile?.cardInfos,
                        cardEvent.bottomSheetDismissListener
                    )
                bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                bottomSheet.show(childFragmentManager, "BlockUnblockCard")
            }
            is CardFragmentVM.CardEvent.SetCardLimitEvent -> {
                val bottomSheet =
                    SetSpendingLimitBottomSheet(
                        cardEvent.bankProfile,
                        cardEvent.onSetSpendingLimitClickListener,
                        cardEvent.bottomSheetDismissListener
                    )
                bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                bottomSheet.show(childFragmentManager, "SetSpendingLimit")

            }
            is CardFragmentVM.CardEvent.ManageChannelEvent -> {
                val bottomSheet =
                    ManageChannelsBottomSheet(
                        cardEvent.bankProfile?.cardInfos,
                        cardEvent.bottomSheetDismissListener
                    )
                bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                bottomSheet.show(childFragmentManager, "ManageChannels")
            }
            is CardFragmentVM.CardEvent.SetPinEvent -> {
                val bottomSheet =
                    SetOrChangePinBottomSheet(cardEvent.setOrChangePinListener)
                bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                bottomSheet.show(childFragmentManager, "SetPin")
            }
            is CardFragmentVM.CardEvent.ActivateCardEvent -> {
                val bottomSheet =
                    ActivateCardBottomSheet(cardEvent.onActivateCardClickListener)
                bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                bottomSheet.show(childFragmentManager, "ActivateCard")

            }
            is CardFragmentVM.CardEvent.SetPinDialogEvent -> {
                val setPinFragment = SetPinDialogFragment(cardEvent.setPinClickListener)
                setPinFragment.show(childFragmentManager, "set pin")
            }
            is CardFragmentVM.CardEvent.PrivacyPolicyTermsConditionEvent -> {
                openWebPageFor(cardEvent.title, cardEvent.url)
            }
            is CardFragmentVM.CardEvent.OnCardNumberCopyEvent -> {
                cardEvent.cardNumber?.let { onCopyClicked(it, requireActivity()) }
            }
        }
    }

    private fun openWebPageFor(title: String, url: String) {
        val intent = Intent(requireActivity(), WebViewActivity::class.java)
        intent.putExtra(ARG_WEB_URL_TO_OPEN, url)
        intent.putExtra(ARG_WEB_PAGE_TITLE, title)
        startActivity(intent)
    }

    private fun callCardSettingsBottomSheet() {
        val bottomSheet =
            CardSettingsBottomSheet(
                cardFragmentVM.bankProfile,
                cardFragmentVM
            )
        bottomSheet.show(childFragmentManager, "CardSettings")
    }

    private fun intentToActivity(aClass: Class<*>, type: String? = null, url: String? = null) {
        val intent = Intent(requireActivity(), aClass)
        if (type == AppConstants.SET_PIN_URL) {
            intent.putExtra(AppConstants.SET_PIN_URL, url)
        }
        requireContext().startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_SECURE
        );
    }

}