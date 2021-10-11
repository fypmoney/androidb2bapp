package com.fypmoney.view.ordercard

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.adapters.ViewBindingAdapter
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.BuildConfig
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewOrderCardBinding
import com.fypmoney.util.Utility
import com.fypmoney.bindingAdapters.setSomePartOfTextInColor
import com.fypmoney.util.AppConstants.ORDER_CARD_INFO
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.ordercard.cardorderoffer.CardOrderOfferActivity
import com.google.common.base.Ascii.RS
import com.google.firebase.analytics.FirebaseAnalytics
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
                trackr { it.services = arrayListOf(TrackrServices.FIREBASE, TrackrServices.MOENGAGE)
                    it.name = TrackrEvent.CHECKOFFER
                    it.add(
                        TrackrField.user_id,SharedPrefUtils.getLong(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_USER_ID
                    ).toString())
                }
                val intent = Intent(this@OrderCardView,CardOrderOfferActivity::class.java)
                intent.putExtra(ORDER_CARD_INFO,mViewModel.userOfferCard)
                startActivity(intent)
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
