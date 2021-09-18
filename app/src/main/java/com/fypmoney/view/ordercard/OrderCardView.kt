package com.fypmoney.view.ordercard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.adapters.ViewBindingAdapter
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewOrderCardBinding
import com.fypmoney.util.Utility
import com.fypmoney.bindingAdapters.setSomePartOfTextInColor
import com.fypmoney.util.AppConstants.ORDER_CARD_INFO
import com.fypmoney.view.ordercard.cardorderoffer.CardOrderOfferActivity
import com.google.common.base.Ascii.RS
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_for_aadhaar.*
import kotlinx.android.synthetic.main.view_order_card.*

/*
* This class is used to order card
* */
class OrderCardView : BaseActivity<ViewOrderCardBinding, OrderCardViewModel>() {

    private lateinit var mViewModel: OrderCardViewModel
    private lateinit var mViewBinding: ViewOrderCardBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_order_card
    }

    override fun getViewModel(): OrderCardViewModel {
        mViewModel = ViewModelProvider(this).get(OrderCardViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@OrderCardView,
            toolbar = toolbar,
            isBackArrowVisible = true,
            backArrowTint = Color.WHITE
        )
        setObservers()

    }

    private fun setObservers() {
        mViewModel.event.observe(this, {
            handelEvent(it)
        })
        mViewModel.state.observe(this, {
            handelState(it)
        })
    }

    private fun handelState(it: OrderCardViewModel.OrderCardState?) {
        when(it){
            is OrderCardViewModel.OrderCardState.Error -> {

            }
            is OrderCardViewModel.OrderCardState.Success -> {
                Utility.convertToRs("${it.userOfferCard.basePrice}")?.let { it1 ->
                    setSomePartOfTextInColor(mViewBinding.cardPriceTv,getString(R.string.fyp_card_price),
                        getString(R.string.Rs)+it1,
                        "#F7AA11"
                    )
                }
            }
        }
    }

    private fun handelEvent(it: OrderCardViewModel.OrderCardEvent?) {
        when (it) {
            OrderCardViewModel.OrderCardEvent.GetOrderCardEvent -> {
                val intent = Intent(this@OrderCardView,CardOrderOfferActivity::class.java)
                intent.putExtra(ORDER_CARD_INFO,mViewModel.userOfferCard)
                startActivity(intent)
            }
        }
    }




}
