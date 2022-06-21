package com.fypmoney.view.giftcard.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentPurchasedGiftCardsHistoryBinding
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

            },
            onRefreshClick = {

            })
    }


    private fun setUpObserver() {

    }


}