package com.fypmoney.view.ordercard.placeordersuccess

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityPlaceOrderSucessBinding
import com.fypmoney.view.activity.HomeView
import com.fypmoney.view.activity.TrackOrderView
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
                    startActivity(Intent(this@PlaceOrderSuccessActivity,TrackOrderView::class.java))
                    finish()
                }
            }
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this@PlaceOrderSuccessActivity, HomeView::class.java))
        finishAffinity()
    }
}