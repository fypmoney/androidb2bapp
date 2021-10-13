package com.fypmoney.view.rewardsAndWinnings.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityScratchProductBinding

import com.fypmoney.model.ScratchCardProductViewmodel

import com.fypmoney.view.customview.scratchlayout.listener.ScratchListener

import com.fypmoney.view.customview.scratchlayout.ui.ScratchCardLayout

import kotlinx.android.synthetic.main.toolbar.*

class ScratchCardActivity :
    BaseActivity<ActivityScratchProductBinding, ScratchCardProductViewmodel>() {
    private lateinit var mViewModel: ScratchCardProductViewmodel
    private lateinit var mBinding: com.fypmoney.databinding.ActivityScratchProductBinding
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_scratch_product

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

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(52)
    }

    private fun handelEvents(it: ScratchCardProductViewmodel.CardOfferEvent?) {
        when (it) {
            ScratchCardProductViewmodel.CardOfferEvent.Continue -> {

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
                mBinding.gotTheOfferIv.invalidate()
                mBinding.offerDescTv.invalidate()
                mBinding.offerAmountTv.invalidate()
                mBinding.gotTheOfferIv.background =
                    AppCompatResources.getDrawable(this@ScratchCardActivity, R.drawable.ic_gift)
                mBinding.offerDescTv.text = getString(R.string.you_won)

            }

            override fun onScratchComplete() {
                mBinding.cardBg.visibility = View.GONE


                mBinding.continueBtn.isEnabled = true
                mBinding.continueBtn.setBusy(false)
            }
        })
    }
}