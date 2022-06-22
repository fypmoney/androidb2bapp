package com.fypmoney.view.giftcard.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.base.PaginationListener
import com.fypmoney.databinding.FragmentPurchasedGiftCardsHistoryBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.view.giftcard.adapters.PurchasedGiftCardItemUiModel
import com.fypmoney.view.giftcard.adapters.PurchasedGiftCardListAdapter
import com.fypmoney.view.giftcard.viewModel.PurchasedGiftCardsHistoryFragmentVM
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import kotlinx.android.synthetic.main.view_bank_transaction_history.*

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
        purchasedGiftCardsHistoryFragmentVM.callGiftCardHistory(0)
    }


    private fun setupView() {
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.gift_card_history),
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.ordersListRv.layoutManager = layoutManager
        binding.ordersListRv.adapter = PurchasedGiftCardListAdapter(
            lifecycleOwner =viewLifecycleOwner,
            onGiftCardClick = {
                purchasedGiftCardsHistoryFragmentVM.onGiftCardClick(it)
            },
            onRefreshClick = {
                purchasedGiftCardsHistoryFragmentVM.callVoucherStatus(it)
            })

        binding.ordersListRv.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                loadMore()
            }

            override fun loadMoreTopItems() {


            }

            override fun isLoading(): Boolean {
                return purchasedGiftCardsHistoryFragmentVM.isLoading
            }
        })
    }


    private fun loadMore() {
        binding.LoadProgressBar.toVisible()
        purchasedGiftCardsHistoryFragmentVM.isLoading = true
        purchasedGiftCardsHistoryFragmentVM.callGiftCardHistory(purchasedGiftCardsHistoryFragmentVM.page)
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
                LoadProgressBar?.visibility = View.GONE
            }
            PurchasedGiftCardsHistoryFragmentVM.PurchasedGiftCardsHistoryState.Error -> {
                binding.ordersListRv.toGone()
                binding.loading.toGone()
                binding.noPurchasedGiftFoundTv.toVisible()
                LoadProgressBar?.visibility = View.GONE
            }
            PurchasedGiftCardsHistoryFragmentVM.PurchasedGiftCardsHistoryState.Loading -> {
                binding.ordersListRv.toGone()
                binding.loading.toVisible()
                binding.noPurchasedGiftFoundTv.toGone()
                LoadProgressBar?.visibility = View.GONE
            }
            is PurchasedGiftCardsHistoryFragmentVM.PurchasedGiftCardsHistoryState.Success -> {
                binding.ordersListRv.toVisible()
                binding.loading.toGone()
                binding.noPurchasedGiftFoundTv.toGone()
                LoadProgressBar?.visibility = View.GONE
                purchasedGiftCardsHistoryFragmentVM.page = purchasedGiftCardsHistoryFragmentVM.page+1
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