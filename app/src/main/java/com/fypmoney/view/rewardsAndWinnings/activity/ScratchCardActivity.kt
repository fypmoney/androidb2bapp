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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.fypmoney.model.SectionListItem
import com.fypmoney.util.Utility
import kotlinx.android.synthetic.main.dialog_burn_mynts.clicked
import kotlinx.android.synthetic.main.dialog_burn_mynts.spin_green
import kotlinx.android.synthetic.main.dialog_burn_mynts.textView
import kotlinx.android.synthetic.main.dialog_cash_won.*
import java.util.ArrayList


class ScratchCardActivity :
    BaseActivity<ActivityScratchProductBinding, ScratchCardProductViewmodel>() {
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

        setToolbarAndTitle(
            context = this@ScratchCardActivity,
            toolbar = toolbar,
            isBackArrowVisible = false,
            backArrowTint = Color.WHITE
        )
        dialogDialog = Dialog(this)

        orderId = intent.getStringExtra(AppConstants.ORDER_ID)
        sectionId = intent.getIntExtra(AppConstants.SECTION_ID, -1)
        var image_url = intent.getStringExtra(AppConstants.PRODUCT_HIDE_IMAGE)

        Glide.with(this).load(image_url).into(mBinding.gotTheOfferIv)
//        mViewModel.callProductsDetailsApi(orderId)
        setUpView()
        mBinding.scratchCardLayout.setScratchDrawable(imageScratch)
        setUpObserver()
    }

    private fun setUpView() {
        setupScratchCardView()
    }

    private fun setUpObserver() {


        mViewModel.scratchResponseList.observe(this) {
//            if(mViewModel.onButtomClicked.value==true){
//
//
//            }
            mBinding.LoadProgressBar.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed({
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    showwonDialog(it.cashbackWon)
                }
            }, 500)


        }
        mViewModel.redeemCallBackResponse.observe(this) {
//          mBinding.scratchCardLayout.setScratchDrawable()

//            Glide.with(this).asDrawable().load(it.scratchResourceHide)
//                .into(object : CustomTarget<Drawable?>() {
//
//                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {
//
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable,
//                        transition: Transition<in Drawable?>?
//                    ) {
//                        mBinding.scratchCardLayout.setScratchDrawable(resource)
//                    }
//                })
//            mBinding.scratchCardLayout.setScratchDrawable(ContextCompat.getDrawable(this,R.drawable.ic_task_rejected))


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
        if (cashbackWon == "0") {
            dialogDialog?.congrats_tv?.visibility = View.GONE
            dialogDialog?.textView?.visibility = View.GONE
            dialogDialog?.clicked?.text = getString(R.string.continue_txt)
            dialogDialog?.luckonside_tv?.visibility = View.GONE
            dialogDialog?.spin_green?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.better_luck_next_time
                )
            )
            dialogDialog?.better_next_time?.visibility = View.VISIBLE
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


                mBinding.offerAmountTv.text = "₹" + Utility.convertToRs(item.sectionValue)

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


                mBinding.LoadProgressBar.visibility = View.VISIBLE

                mViewModel.callScratchWheelApi(orderId, false)


            }
        })
    }
}