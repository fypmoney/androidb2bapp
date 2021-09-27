package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityCardOrderOfferBinding
import com.fypmoney.model.ScratchCardProductViewmodel

import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.customview.scratchlayout.listener.ScratchListener
import com.fypmoney.view.customview.scratchlayout.ui.ScratchCard
import com.fypmoney.view.customview.scratchlayout.ui.ScratchCardLayout

import kotlinx.android.synthetic.main.toolbar.*

class ScratchCardActivity :
    BaseActivity<ActivityCardOrderOfferBinding, ScratchCardProductViewmodel>() {
    private lateinit var mViewModel: ScratchCardProductViewmodel
    private lateinit var mBinding: ActivityCardOrderOfferBinding
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_card_order_offer

    override fun getViewModel(): ScratchCardProductViewmodel {
        mViewModel = ViewModelProvider(this).get(ScratchCardProductViewmodel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = this@ScratchCardActivity,
            toolbar = toolbar,
            isBackArrowVisible = true,
            backArrowTint = Color.WHITE
        )

        setUpView()
        setUpObserver()
    }

    private fun setUpView() {

        setupScratchCardView()


    }

    private fun setUpObserver() {
        mViewModel.event.observe(this, {
            handelEvents(it)
        })
    }

    private fun handelEvents(it: CardOrderOfferVM.CardOfferEvent?) {
        when (it) {
            CardOrderOfferVM.CardOfferEvent.Continue -> {

            }

        }
    }

    private fun setupScratchCardView() {
        mBinding.scratchCardLayout.setScratchListener(object : ScratchListener {
            override fun onScratchStarted() {
            }

            override fun onScratchProgress(
                scratchCardLayout: ScratchCardLayout,
                atLeastScratchedPercent: Int
            ) {
                mBinding.continueBtn.setBusy(true)

            }

            override fun onScratchComplete() {
                SharedPrefUtils.putBoolean(
                    this@ScratchCardActivity,
                    SharedPrefUtils.SF_KEY_IS_ORDER_SCARTCH_CODE_DONE,
                    true
                )
                if (mViewModel.userOfferCard?.discount!! > 0) {
                    mBinding.offerFoundTv.visibility = View.VISIBLE
                }
                mBinding.continueBtn.isEnabled = true
                mBinding.continueBtn.setBusy(false)
            }
        })
    }
}