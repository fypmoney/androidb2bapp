package com.fypmoney.view.ordercard

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants.ORDER_CARD_INFO
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.ordercard.activateofflinecard.ScanCardKitNumberActivity
import com.fypmoney.view.ordercard.cardofferdetails.CardOfferDetailsActivity
import com.fypmoney.view.ordercard.promocode.ApplyPromoCodeBottomSheet
import kotlinx.android.synthetic.main.toolbar.*

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


        setObservers()
    }

    override fun onStart() {
        super.onStart()
        checkPromoCodeIsApplied()?.let {
            if(it){
                mViewBinding.havePromoCodeTv.toGone()
            }else{
                mViewBinding.havePromoCodeTv.toVisible()
            }
        }
    }
    private fun checkPromoCodeIsApplied():Boolean?{
        return SharedPrefUtils.getBoolean(this,SharedPrefUtils.SF_CARD_PROMO_CODE_APPLIED)
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
                it.userOfferCard.mrp.toIntOrNull()?.let {
                    if(it==0){
                        mViewBinding.havePromoCodeTv.toGone()
                    }
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
            OrderCardViewModel.OrderCardEvent.HaveAPromoCode -> {
                val applyPromoCodeBottomSheet = ApplyPromoCodeBottomSheet(promoCodeAppliedSuccessfully = {
                    val intent = Intent(this@OrderCardView, CardOfferDetailsActivity::class.java)
                    intent.putExtra(ORDER_CARD_INFO,it)
                    startActivity(intent)
                })
                applyPromoCodeBottomSheet.dialog?.window?.setBackgroundDrawable(
                    ColorDrawable(
                        Color.RED)
                )
                applyPromoCodeBottomSheet.show(supportFragmentManager, "ApplyPromoCode")
            }

        }
    }



}
