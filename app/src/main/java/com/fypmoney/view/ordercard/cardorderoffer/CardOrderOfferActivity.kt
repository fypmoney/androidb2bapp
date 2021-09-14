package com.fypmoney.view.ordercard.cardorderoffer

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityCardOrderOfferBinding
import com.fypmoney.view.customview.scratchlayout.listener.ScratchListener
import com.fypmoney.view.customview.scratchlayout.ui.ScratchCardLayout
import com.fypmoney.view.ordercard.OrderCardViewModel
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

        setupScratchCardView()
        setUpObserver()
    }

    private fun setUpObserver() {
        mViewModel.event.observe(this,{
            handelEvents(it)
        })
    }

    private fun handelEvents(it: CardOrderOfferVM.CardOfferEvent?) {
        when(it){
            CardOrderOfferVM.CardOfferEvent.Continue -> {
                val intent = Intent(this@CardOrderOfferActivity,PersonaliseYourCardActivity::class.java)
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
            }

            override fun onScratchComplete() {
            }
        })
    }
}