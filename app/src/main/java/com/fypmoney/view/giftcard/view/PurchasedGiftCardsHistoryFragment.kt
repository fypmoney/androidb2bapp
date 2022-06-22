package com.fypmoney.view.giftcard.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentPurchasedGiftCardsHistoryBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.view.giftcard.adapters.PurchasedGiftCardItemUiModel
import com.fypmoney.view.giftcard.adapters.PurchasedGiftCardListAdapter
import com.fypmoney.view.giftcard.viewModel.PurchasedGiftCardsHistoryFragmentVM
import kotlinx.android.synthetic.main.toolbar_gift_card.*

class PurchasedGiftCardsHistoryFragment : BaseFragment<FragmentPurchasedGiftCardsHistoryBinding,PurchasedGiftCardsHistoryFragmentVM>() {

    companion object {
        fun newInstance() = PurchasedGiftCardsHistoryFragment()
    }

    private  val purchasedGiftCardsHistoryFragmentVM by viewModels<PurchasedGiftCardsHistoryFragmentVM> { defaultViewModelProviderFactory }

    private lateinit var binding: FragmentPurchasedGiftCardsHistoryBinding

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
    override fun getLayoutId(): Int  = R.layout.fragment_purchased_gift_cards_history

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): PurchasedGiftCardsHistoryFragmentVM  = purchasedGiftCardsHistoryFragmentVM


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        setupView()
        setupRecyclerView()
        setUpObserver()
        purchasedGiftCardsHistoryFragmentVM.callGiftCardHistory()
    }


    private fun setupView() {
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Gift Card History",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
    }

    private fun setupRecyclerView() {
        binding.ordersListRv.adapter = PurchasedGiftCardListAdapter(
            lifecycleOwner =viewLifecycleOwner,
            onGiftCardClick = {
                purchasedGiftCardsHistoryFragmentVM.onGiftCardClick(it)
            },
            onRefreshClick = {
                purchasedGiftCardsHistoryFragmentVM.callVoucherStatus(it)
            })
    }


    private fun setUpObserver() {
        purchasedGiftCardsHistoryFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        purchasedGiftCardsHistoryFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelState(it: PurchasedGiftCardsHistoryFragmentVM.PurchasedGiftCardsHistoryState?) {
        when(it){
            PurchasedGiftCardsHistoryFragmentVM.PurchasedGiftCardsHistoryState.Empty -> {
                binding.ordersListRv.toGone()
                binding.loading.toGone()
                binding.noPurchasedGiftFoundTv.toVisible()
            }
            PurchasedGiftCardsHistoryFragmentVM.PurchasedGiftCardsHistoryState.Error -> {
                binding.ordersListRv.toGone()
                binding.loading.toGone()
                binding.noPurchasedGiftFoundTv.toVisible()
            }
            PurchasedGiftCardsHistoryFragmentVM.PurchasedGiftCardsHistoryState.Loading -> {
                binding.ordersListRv.toGone()
                binding.loading.toVisible()
                binding.noPurchasedGiftFoundTv.toGone()
            }
            is PurchasedGiftCardsHistoryFragmentVM.PurchasedGiftCardsHistoryState.Success -> {
                binding.ordersListRv.toVisible()
                binding.loading.toGone()
                binding.noPurchasedGiftFoundTv.toGone()
                (binding.ordersListRv.adapter as PurchasedGiftCardListAdapter).submitList(
                    it.list.map {
                        it.let { it1 ->
                            PurchasedGiftCardItemUiModel.convertGiftCardHistoryItemToPurchasedGiftCardItemUiModel(
                                context =requireContext(), giftCardHistoryItem = it1
                            )
                        }
                    }
                )
            }
            null -> {

            }
        }
    }

    private fun handelEvent(it: PurchasedGiftCardsHistoryFragmentVM.PurchasedGiftCardsHistoryEvent?) {
        when(it){
            is PurchasedGiftCardsHistoryFragmentVM.PurchasedGiftCardsHistoryEvent.NavigateToGiftCardDetail -> {
                findNavController().navigate(PurchasedGiftCardsHistoryFragmentDirections.actionGiftCardHistoryToGiftCardDetails(it.giftDetailsId))
            }
            null -> {

            }
        }
    }


}