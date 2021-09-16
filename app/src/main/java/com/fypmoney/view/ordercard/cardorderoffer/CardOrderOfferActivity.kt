package com.fypmoney.view.ordercard.cardorderoffer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityCardOrderOfferBinding
import com.fypmoney.util.AppConstants.ORDER_CARD_INFO
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.customview.scratchlayout.listener.ScratchListener
import com.fypmoney.view.customview.scratchlayout.ui.ScratchCardLayout
import com.fypmoney.view.ordercard.personaliseyourcard.PersonaliseYourCardActivity
import kotlinx.android.synthetic.main.toolbar.*

class CardOrderOfferActivity : BaseActivity<ActivityCardOrderOfferBinding,CardOrderOfferVM>() {
    private lateinit var mViewModel: CardOrderOfferVM
    private lateinit var mBinding: ActivityCardOrderOfferBinding
    override fun getBindingVariable(): Int  =  BR.viewModel

    override fun getLayoutId(): Int  = R.layout.activity_card_order_offer

    override fun getViewModel(): CardOrderOfferVM {
        mViewModel = ViewModelProvider(this).get(CardOrderOfferVM::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = this@CardOrderOfferActivity,
            toolbar = toolbar,
            isBackArrowVisible = true,
            backArrowTint = Color.WHITE
        )
        mViewModel.userOfferCard = intent.getParcelableExtra(ORDER_CARD_INFO)
        /*mViewModel.userOfferCard = usercard?.apply {
            this.discount  = 0
        }*/
        setUpView()
        setUpObserver()
    }

    private fun setUpView() {
        mViewModel.userOfferCard?.let {
            if(it.discount>0){
                mBinding.gotTheOfferIv.invalidate()
                mBinding.offerDescTv.invalidate()
                mBinding.offerAmountTv.invalidate()
                mBinding.gotTheOfferIv.background = AppCompatResources.getDrawable(this,R.drawable.ic_gift)
                mBinding.offerDescTv.text = getString(R.string.you_won)
                mBinding.offerAmountTv.text =
                    """${getString(R.string.Rs)}${Utility.convertToRs(it.discount.toString())}"""
            }else{
                mBinding.gotTheOfferIv.invalidate()
                mBinding.gotTheOfferIv.background = AppCompatResources.getDrawable(this,R.drawable.ic_oops_emoji)
                mBinding.offerDescTv.invalidate()
                mBinding.offerDescTv.text = getString(R.string.opps)
                mBinding.offerDescTv.textSize = 27.0f
                mBinding.offerDescTv.setTextColor(ContextCompat.getColor(this,R.color.text_color_dark))
                mBinding.offerAmountTv.invalidate()
                mBinding.offerAmountTv.text =getString(R.string.better_luck_next_time)
                mBinding.offerAmountTv.textSize = 14.0f
                mBinding.offerAmountTv.setTextColor(ContextCompat.getColor(this,R.color.text_color_light))
            }
        }
        setupScratchCardView()
        SharedPrefUtils.getBoolean(this,
            SharedPrefUtils.SF_KEY_IS_ORDER_SCARTCH_CODE_DONE
        )?.let {
            if(it){
                mBinding.scratchCardLayout.revealScratch()
            }
        }

    }

    private fun setUpObserver() {
        mViewModel.event.observe(this,{
            handelEvents(it)
        })
    }

    private fun handelEvents(it: CardOrderOfferVM.CardOfferEvent?) {
        when(it){
            CardOrderOfferVM.CardOfferEvent.Continue -> {
                SharedPrefUtils.putBoolean(this,
                    SharedPrefUtils.SF_KEY_IS_ORDER_SCARTCH_CODE_DONE,
                    true
                )
                val intent = Intent(this@CardOrderOfferActivity,PersonaliseYourCardActivity::class.java)
                intent.putExtra(ORDER_CARD_INFO,mViewModel.userOfferCard)
                startActivity(intent)
            }

        }
    }

    private fun setupScratchCardView() {
        mBinding.scratchCardLayout.setScratchListener(object :ScratchListener{
            override fun onScratchStarted() {
            }

            override fun onScratchProgress(
                scratchCardLayout: ScratchCardLayout,
                atLeastScratchedPercent: Int
            ) {
                mBinding.continueBtn.setBusy(true)

            }

            override fun onScratchComplete() {
                mBinding.continueBtn.isEnabled = true
                mBinding.continueBtn.setBusy(false)
            }
        })
    }
}