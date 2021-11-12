package com.fypmoney.view.rewardsAndWinnings.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityScratchProductBinding

import com.fypmoney.model.ScratchCardProductViewmodel
import com.fypmoney.util.AppConstants

import com.fypmoney.view.customview.scratchlayout.listener.ScratchListener

import com.fypmoney.view.customview.scratchlayout.ui.ScratchCardLayout


import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_spin_wheel_black.*
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.model.SectionListItem
import com.fypmoney.util.Utility
import kotlinx.android.synthetic.main.dialog_burn_mynts.clicked
import kotlinx.android.synthetic.main.dialog_burn_mynts.spin_green
import kotlinx.android.synthetic.main.dialog_burn_mynts.textView
import kotlinx.android.synthetic.main.dialog_cash_won.*
import kotlinx.android.synthetic.main.dialog_cash_won.better_next_time
import kotlinx.android.synthetic.main.dialog_cash_won.luckonside_tv
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*
import java.util.ArrayList


class ScratchCardActivity :
    BaseActivity<ActivityScratchProductBinding, ScratchCardProductViewmodel>() {
    private var noOfGoldenCard: Int? = null
    private var ProductCode: String? = null
    private var dialogError: Dialog? = null
    private var sectionId: Int? = null
    private var orderId: String? = null
    private lateinit var mViewModel: ScratchCardProductViewmodel
    private lateinit var mBinding: com.fypmoney.databinding.ActivityScratchProductBinding
    override fun getBindingVariable(): Int = BR.viewModel
    private var dialogDialog: Dialog? = null
    override fun getLayoutId(): Int = R.layout.activity_scratch_product

    companion object {
        var imageScratch: Drawable? = null
        var sectionArrayList: ArrayList<SectionListItem> = ArrayList()
    }


    override fun getViewModel(): ScratchCardProductViewmodel {
        mViewModel = ViewModelProvider(this).get(ScratchCardProductViewmodel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        dialogError = Dialog(this)

        setToolbarAndTitle(
            context = this@ScratchCardActivity,
            toolbar = toolbar,
            isBackArrowVisible = false,
            backArrowTint = Color.WHITE
        )
        dialogDialog = Dialog(this)

        orderId = intent.getStringExtra(AppConstants.ORDER_NUM)
        sectionId = intent.getIntExtra(AppConstants.SECTION_ID, -1)
        ProductCode = intent.getStringExtra(AppConstants.PRODUCT_CODE)
        noOfGoldenCard = intent.getIntExtra(AppConstants.NO_GOLDED_CARD, -1)

        var image_url = intent.getStringExtra(AppConstants.PRODUCT_HIDE_IMAGE)

        Glide.with(this).load(image_url).into(mBinding.gotTheOfferIv)
//        mViewModel.callProductsDetailsApi(orderId)

        setupScratchCardView()
        mBinding.scratchCardLayout.setScratchDrawable(imageScratch)
        setUpObserver()
    }


    private fun callErrorDialog(msg: String) {
        dialogError?.setCancelable(false)
        dialogError?.setCanceledOnTouchOutside(false)
        dialogError?.setContentView(R.layout.dialog_rewards_error_scratch)

        val wlp = dialogError?.window?.attributes

        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialogError?.setCanceledOnTouchOutside(false)





        dialogError?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogError?.window?.attributes = wlp
        dialogError?.error_msg?.text = msg



        dialogError?.clicked?.setOnClickListener(View.OnClickListener {
         finish()
        })


        dialogError?.show()
    }

    private fun setUpObserver() {
        mViewModel?.error?.observe(
            this,
            androidx.lifecycle.Observer { list ->

                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    dialogDialog?.dismiss()
                    callErrorDialog(list.msg)
                }

            }
        )


        mViewModel.scratchResponseList.observe(this) {
//            if(mViewModel.onButtomClicked.value==true){
//
//
//            }
            mBinding.LoadProgressBar.visibility = View.GONE

            Handler(Looper.getMainLooper()).postDelayed({
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    trackr {

                        it.name = TrackrEvent.scratch_success
                        it.add(TrackrField.spin_product_code, ProductCode)

                    }
                    showwonDialog(it.cashbackWon, it.noOfJackpotTicket)
                }
            }, 500)


        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(52)
    }

    private fun showwonDialog(cashbackWon: String?, noOfJackpotTicket: Int?) {


        dialogDialog?.setCancelable(false)
        dialogDialog?.setCanceledOnTouchOutside(false)
        dialogDialog?.setContentView(R.layout.dialog_cash_won)

        val wlp = dialogDialog?.window?.attributes

        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        if (cashbackWon == "0") {
            dialogDialog?.congrats_tv?.visibility = View.GONE
            dialogDialog?.textView?.visibility = View.GONE
            dialogDialog?.spin_green?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.golden_cards
                )
            )
            dialogDialog?.better_next_time?.visibility = View.INVISIBLE
            if (noOfJackpotTicket != null) {

                dialogDialog!!.golden_cards_won!!.text =
                    "You won " + noOfJackpotTicket + "\nGolden Tickets"
            }
            dialogDialog?.clicked?.text = getString(R.string.continue_txt)
            dialogDialog?.luckonside_tv?.visibility = View.GONE

        }
        if (mViewModel.played.get() == true) {
            dialogDialog?.textView?.text =
                "your wallet has been updated with ₹ " + Utility.convertToRs(cashbackWon)

        } else {
            dialogDialog?.textView?.text =
                "your wallet will be updated with ₹ " + Utility.convertToRs(cashbackWon)
        }

        dialogDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogDialog?.window?.attributes = wlp

        dialogDialog?.clicked?.text = getString(R.string.continue_txt)

        dialogDialog?.clicked?.setOnClickListener(View.OnClickListener {
            if (mViewModel.played.get() == true) {
//                if (cashbackWon == "0") {
//                    trackr {
//                        it.name = TrackrEvent.SPINZERO
//                        it.add(TrackrField.spin_product_code,ProductCode)
//
//                    }
//                }
                finish()
            } else {
                mViewModel.callScratchWheelApi(orderId, true)
            }
        })


        dialogDialog?.show()
    }


    private fun setupScratchCardView() {
        sectionArrayList?.forEach { item ->

            if (item?.id == sectionId.toString()) {

                if (item.sectionValue != null && item.sectionValue != "0") {
                    mBinding.offerAmountTv.text = "₹" + Utility.convertToRs(item.sectionValue)
                    mBinding.offerDescTv.visibility = View.VISIBLE
                } else {
                    if (noOfGoldenCard != null) {
                        mBinding.offerAmountTv.text = noOfGoldenCard.toString() + " Golden Tickets"

                    }
                    mBinding.offerDescTv.setTextColor(ContextCompat.getColor(this, R.color.white))
                    mBinding.offerAmountTv.setTextColor(ContextCompat.getColor(this, R.color.white))
//                    mBinding.oppsTv.visibility = View.VISIBLE
//                    mBinding.betterLuck.visibility = View.VISIBLE


                    mBinding.smileyOops.visibility = View.VISIBLE
                    Glide.with(this).load(R.color.white).into(mBinding.gotTheOfferIv)

                }




                return@forEach

            }


        }
        mBinding.scratchCardLayout.setScratchListener(object : ScratchListener {
            override fun onScratchStarted() {
            }

            override fun onScratchProgress(
                scratchCardLayout: ScratchCardLayout,
                atLeastScratchedPercent: Int
            ) {


            }

            override fun onScratchComplete() {


                mBinding.offerFoundTv.visibility = View.VISIBLE
                mBinding.LoadProgressBar.visibility = View.VISIBLE
                mViewModel.callScratchWheelApi(orderId, false)


            }
        })
    }
}