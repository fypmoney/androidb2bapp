package com.fypmoney.view.giftcard.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentGiftCardDetailsBinding
import com.fypmoney.extension.toVisible
import com.fypmoney.view.giftcard.viewModel.GiftCardDetailsFragmentVM
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*

class GiftCardDetailsFragment : BaseFragment<FragmentGiftCardDetailsBinding, GiftCardDetailsFragmentVM>() {

    companion object {
        fun newInstance() = GiftCardDetailsFragment()
    }

    private val giftCardDetailsFragmentVM  by viewModels<GiftCardDetailsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentGiftCardDetailsBinding

    override fun onTryAgainClicked() {
        TODO("Not yet implemented")
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
        setUpViews()
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
        binding.layout.toolbar_image.setOnClickListener {  }
    }

}