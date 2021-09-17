package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewTrackOrderBinding
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.TrackOrderViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_for_aadhaar.*
import kotlinx.android.synthetic.main.view_order_card.*
import kotlinx.android.synthetic.main.view_track_order.*

/*
* This class is used to card tracking
* */
class TrackOrderView : BaseActivity<ViewTrackOrderBinding, TrackOrderViewModel>() {
    private lateinit var mViewModel: TrackOrderViewModel
    private lateinit var binding: ViewTrackOrderBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_track_order
    }

    override fun getViewModel(): TrackOrderViewModel {
        mViewModel = ViewModelProvider(this).get(TrackOrderViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@TrackOrderView,
            toolbar = toolbar,
            isBackArrowVisible = true,toolbarTitle=getString(R.string.track_order),backArrowTint = Color.WHITE,titleColor = Color.WHITE
        )

        helpValue.text = getString(R.string.aadhaar_number_help_text)
        helpValue.setOnClickListener {
            callFreshChat(applicationContext)

        }
        setObservers()
    }

    fun setObservers() {
        mViewModel.productResponse.observe(this)
        {
            binding.amount.text = "${getString(R.string.Rs)} ${Utility.convertToRs(it.basePrice.toString())}"
            binding.discountAmountTv.text = "${getString(R.string.Rs)} ${Utility.convertToRs(it.discount.toString())}"
            binding.netPaybleAmountTv.text = "${getString(R.string.Rs)} ${Utility.convertToRs(it.amount.toString())}"
            binding.taxAmountTv.text = String.format(getString(R.string.inc_tax), Utility.convertToRs(it.totalTax.toString()))

        }

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@TrackOrderView, aClass))
    }

   /* *//*
    * This is used to set data
    * *//*
    private fun setDataInUi() {
        when (mViewModel.orderStatus.get()) {
            AppConstants.ORDER_STATUS_ORDERED -> {
                textOrderPlaced.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.text_color_dark
                    )
                )


            }
            AppConstants.ORDER_STATUS_SHIPPED -> {
                imageOrderShipped.setImageResource(R.drawable.ic_check_skyblue)
                view_order_placed.setImageResource(R.drawable.dotted_skyblue)
                textOrderShipped.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.text_color_dark
                    )
                )


            }
            AppConstants.ORDER_STATUS_DISPATCHED -> {
                imageOrderShipped.setImageResource(R.drawable.ic_check_skyblue)
                imageOrderDelivery.setImageResource(R.drawable.ic_check_skyblue)
                view_order_placed.setImageResource(R.drawable.dotted_skyblue)
                view_order_Shipped.setImageResource(R.drawable.dotted_skyblue)
                textOrderDelivery.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.text_color_dark
                    )
                )


            }
            AppConstants.ORDER_STATUS_DELIVERED -> {
                imageOrderShipped.setImageResource(R.drawable.ic_check_skyblue)
                imageOrderDelivery.setImageResource(R.drawable.ic_check_skyblue)
                imageOrderDelivered.setImageResource(R.drawable.ic_check_skyblue)
                view_order_placed.setImageResource(R.drawable.dotted_skyblue)
                view_order_Shipped.setImageResource(R.drawable.dotted_skyblue)
                view_order_Delivery.setImageResource(R.drawable.dotted_skyblue)
                textOrderDelivered.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.text_color_dark
                    )
                )


            }
        }


    }
*/
}
