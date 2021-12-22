package com.fypmoney.view.ordercard

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewOrderCardBinding
import com.fypmoney.util.AppConstants.ORDER_CARD_INFO
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.ordercard.activateofflinecard.ScanCardKitNumberActivity
import com.fypmoney.view.ordercard.cardofferdetails.CardOfferDetailsActivity
import kotlinx.android.synthetic.main.activity_notify_me_order_card.*
import kotlinx.android.synthetic.main.screen_card.*
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
            backArrowTint = Color.WHITE,
            titleColor = Color.BLACK
        )

        if(SharedPrefUtils.getString(
                applicationContext,
                SharedPrefUtils.SF_KEY_CARD_FLAG
            )=="1"){
            showNotifyCardLayout()
        }else{
            mViewBinding.notifyOrderCardNsv.visibility = View.GONE
            mViewBinding.orderCardNsv.visibility = View.VISIBLE

            val uri: Uri =
                Uri.parse("android.resource://" + packageName + "/" + R.raw.ic_card_video)
            mViewBinding.cardFrontAiv.setMediaController(null)
            mViewBinding.cardFrontAiv.setVideoURI(uri)
            mViewBinding.cardFrontAiv.setOnPreparedListener {
                it.isLooping = true
                mViewBinding.cardFrontAiv.start()
            }

        }
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
                onBackPressed()
            }
            is OrderCardViewModel.OrderCardState.Success -> {
                Utility.convertToRs("${it.userOfferCard.basePrice}")?.let { it1 ->
                    mViewBinding.cardActualPriceValueTv.text = getString(R.string.Rs)+it1
                }
                Utility.convertToRs("${it.userOfferCard.mrp}")?.let { it1 ->
                    mViewBinding.cardOfferPriceValueTv.text = getString(R.string.Rs)+it1
                }
            }
        }
    }

    private fun handelEvent(it: OrderCardViewModel.OrderCardEvent?) {
        when (it) {
            OrderCardViewModel.OrderCardEvent.GetOrderCardEvent -> {
                trackr {
                    it.name = TrackrEvent.order_now
                    it.add(
                        TrackrField.user_id,SharedPrefUtils.getLong(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_USER_ID
                    ).toString())
                }
                val intent = Intent(this@OrderCardView, CardOfferDetailsActivity::class.java)
                intent.putExtra(ORDER_CARD_INFO,mViewModel.userOfferCard)
                startActivity(intent)
            }
            OrderCardViewModel.OrderCardEvent.AlreadyHaveACardEvent -> {
                val intent = Intent(this@OrderCardView,ScanCardKitNumberActivity::class.java)
                intent.putExtra(ORDER_CARD_INFO,mViewModel.userOfferCard)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun showNotifyCardLayout() {
        val uri: Uri =
            Uri.parse("android.resource://" + packageName + "/" + R.raw.notify_order_card)
        mViewBinding.video.setMediaController(null)
        mViewBinding.video.setVideoURI(uri)
        mViewBinding.video.setOnPreparedListener {
            it.isLooping = true
            mViewBinding.video.start()
        }
        mViewBinding.notifyBtn.setOnClickListener {
            Utility.showToast(resources.getString(R.string.thanks_we_will_keep_you_notify))
            finish()
        }
        mViewBinding.notifyOrderCardNsv.visibility = View.VISIBLE
    }


}
