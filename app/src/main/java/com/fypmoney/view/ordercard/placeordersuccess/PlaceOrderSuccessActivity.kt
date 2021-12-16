package com.fypmoney.view.ordercard.placeordersuccess

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityPlaceOrderSucessBinding
import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.view.ordercard.trackorder.TrackOrderView
import kotlinx.android.synthetic.main.activity_place_order_sucess.*
import kotlinx.android.synthetic.main.toolbar.*

class PlaceOrderSuccessActivity : BaseActivity<ActivityPlaceOrderSucessBinding,PlaceOrderSuccessVM>() {
    lateinit var mViewModel:PlaceOrderSuccessVM


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int  = R.layout.activity_place_order_sucess

    override fun getViewModel(): PlaceOrderSuccessVM {
        mViewModel = ViewModelProvider(this).get(PlaceOrderSuccessVM::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@PlaceOrderSuccessActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",  backArrowTint = Color.WHITE,
            titleColor = Color.WHITE
        )


        setUpObserver()
    }

    private fun setUpObserver() {
        mViewModel.event.observe(this,{
            when(it){
                PlaceOrderSuccessVM.PlaceOrderSuccessEvent.TrackOrderEvent -> {
                    val i = Intent(this@PlaceOrderSuccessActivity, TrackOrderView::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(i)
                    finish()
                }
            }
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this@PlaceOrderSuccessActivity, HomeActivity::class.java))
    }
}