package com.fypmoney.view.ordercard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewOrderCardBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.PlaceOrderCardView
import com.fypmoney.view.ordercard.cardorderoffer.CardOrderOfferActivity
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
        mViewBinding.cardPriceTv.text = String.format(getString(R.string.fyp_card_price),Utility.convertToRs("20000"))
        setObservers()

    }

    private fun setObservers() {
        mViewModel.event.observe(this, {
            handelEvent(it)
        })
    }

    private fun handelEvent(it: OrderCardViewModel.OrderCardEvent?) {
        when (it) {
            OrderCardViewModel.OrderCardEvent.GetOrderCardEvent -> {
                intentToActivity(CardOrderOfferActivity::class.java)
            }
        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@OrderCardView, aClass))
    }


}
