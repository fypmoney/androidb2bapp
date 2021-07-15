package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewPlaceCardBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.PriceBreakupBottomSheet
import com.fypmoney.viewmodel.PlaceOrderCardViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_order_card.*

/*
* This class is used to order card
* */
class PlaceOrderCardView : BaseActivity<ViewPlaceCardBinding, PlaceOrderCardViewModel>() {
    private lateinit var mViewModel: PlaceOrderCardViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_place_card
    }

    override fun getViewModel(): PlaceOrderCardViewModel {
        mViewModel = ViewModelProvider(this).get(PlaceOrderCardViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@PlaceOrderCardView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )

        setObservers()
    }

    fun setObservers() {
        mViewModel.onPriceBreakupClicked.observe(this)
        {
            if (it) {
                callPriceBreakupBottomSheet()
                mViewModel.onPriceBreakupClicked.value = false
            }
        }

        mViewModel.onPlaceOrderClicked.observe(this)
        {
            if (it) {
                askForDevicePassword()
                mViewModel.onPlaceOrderClicked.value = false
            }
        }
        mViewModel.onOrderCardSuccess.observe(this)
        {
            intentToActivity(TrackOrderView::class.java)

        }
    }

    /*
   * This method is used to call card settings
   * */
    private fun callPriceBreakupBottomSheet() {
        val bottomSheet =
            PriceBreakupBottomSheet()
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "PlaceOrderBottomSheet")
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                  RESULT_OK -> {
                        Handler(Looper.getMainLooper()).post {
                            mViewModel.callPlaceOrderApi()

                        }

                  }

                }
            }
        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@PlaceOrderCardView, aClass))
    }


}
