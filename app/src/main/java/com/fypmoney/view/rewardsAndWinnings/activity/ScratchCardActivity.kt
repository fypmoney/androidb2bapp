package com.fypmoney.view.rewardsAndWinnings.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityScratchProductBinding

import com.fypmoney.model.ScratchCardProductViewmodel
import com.fypmoney.util.AppConstants

import com.fypmoney.view.customview.scratchlayout.listener.ScratchListener

import com.fypmoney.view.customview.scratchlayout.ui.ScratchCardLayout
import kotlinx.android.synthetic.main.dialog_burn_mynts.*

import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_spin_wheel_black.*
import android.os.Handler
import android.os.Looper
import androidx.annotation.Nullable
import com.fypmoney.util.Utility


class ScratchCardActivity :
    BaseActivity<ActivityScratchProductBinding, ScratchCardProductViewmodel>() {
    private var sectionId: Int? = null
    private var orderId: String? = null
    private lateinit var mViewModel: ScratchCardProductViewmodel
    private lateinit var mBinding: com.fypmoney.databinding.ActivityScratchProductBinding
    override fun getBindingVariable(): Int = BR.viewModel
    private var dialogDialog: Dialog? = null
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
        dialogDialog = Dialog(this)

        orderId = intent.getStringExtra(AppConstants.ORDER_ID)
        sectionId = intent.getIntExtra(AppConstants.SECTION_ID, -1)
        mViewModel.callProductsDetailsApi(orderId)
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
        mViewModel.spinWheelResponseList.observe(this) {
//            if(mViewModel.onButtomClicked.value==true){
//
//
//            }
            Handler(Looper.getMainLooper()).postDelayed({
                showwonDialog(it.cashbackWon)
            }, 1000)
            mBinding.continueBtn.setBusy(false)
            mBinding.continueBtn.isEnabled = false
            mBinding.continueBtn.setText("Claimed")


        }
        mViewModel.redeemCallBackResponse.observe(this) {
//          mBinding.scratchCardLayout.setScratchDrawable()

            mBinding.bodyTv.text = it.description

            Glide.with(this).load(it.scratchResourceShow).into(mBinding.gotTheOfferIv)

            Glide.with(this).asDrawable().load(it.scratchResourceHide)
                .into(object : CustomTarget<Drawable?>() {

                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {}


                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        mBinding.scratchCardLayout.setScratchDrawable(resource)
                    }
                })


        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(52)
    }

    private fun showwonDialog(cashbackWon: String?) {


        dialogDialog?.setCancelable(false)
        dialogDialog?.setCanceledOnTouchOutside(false)
        dialogDialog?.setContentView(R.layout.dialog_cash_won)

        val wlp = dialogDialog?.window?.attributes

        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT


        dialogDialog?.textView?.text =
            "your wallet has been updated with ₹ " + Utility.convertToRs(cashbackWon)
        dialogDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogDialog?.window?.attributes = wlp

        dialogDialog?.clicked?.text = getString(R.string.continue_txt)

        dialogDialog?.clicked?.setOnClickListener(View.OnClickListener {
            finish()
        })


        dialogDialog?.show()
    }

    private fun handelEvents(it: ScratchCardProductViewmodel.CardOfferEvent?) {
        when (it) {
            ScratchCardProductViewmodel.CardOfferEvent.Continue -> {
                mViewModel.onButtomClicked.value = true
                mBinding.continueBtn.setBusy(false)

                mViewModel.callScratchWheelApi(orderId, true)

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

                mViewModel.callScratchWheelApi(orderId, false)
                mBinding.continueBtn.isEnabled = true
                mBinding.continueBtn.setBusy(false)
            }
        })
    }
}