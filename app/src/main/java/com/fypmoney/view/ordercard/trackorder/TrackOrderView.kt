package com.fypmoney.view.ordercard.trackorder

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewTrackOrderBinding
import com.fypmoney.model.PackageStatusList
import com.fypmoney.util.Utility

import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.fypmoney.view.webview.WebViewActivity
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
        mViewModel.event.observe(this,{
            when(it){
                is TrackOrderViewModel.TrackOrderEvent.ShippingDetailsEvent ->{
                    callShippingDetails(it.packageStatusList)
                }
            }
        })

    }

    private fun callShippingDetails(packageStatusList: PackageStatusList?) {
        val bottomSheet =
                packageStatusList?.let { ShippingDetailsBottomSheet(it,onTrackOrderClick = {
                    it.link?.let { it1 -> openWebPageFor("Track Order", it1) }
                }) }
            bottomSheet?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
            bottomSheet?.show(supportFragmentManager, "Shipping Details")


    }



    override fun onBackPressed() {
        startActivity(Intent(this@TrackOrderView, HomeActivity::class.java))
        finish()
    }

}
