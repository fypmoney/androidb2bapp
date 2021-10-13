package com.fypmoney.view.rewardsAndWinnings.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewSpinWheelBlackBinding
import com.fypmoney.model.SectionListItem
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.ErrorBottomSpinProductSheet
import com.fypmoney.view.rewardsAndWinnings.viewModel.SpinWheelProductViewModel
import kotlinx.android.synthetic.main.dialog_burn_mynts.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_spin_wheel_black.*


import java.util.*

/*
* This class is used for spin the wheel
* */
class SpinWheelViewDark : BaseActivity<ViewSpinWheelBlackBinding, SpinWheelProductViewModel>(),
    ErrorBottomSpinProductSheet.OnSpinErrorClickListener {

    private var dialogDialog: Dialog? = null
    private var orderId: String? = null

    private lateinit var mViewModel: SpinWheelProductViewModel

    companion object {
        var sectionArrayList: ArrayList<SectionListItem> = ArrayList()
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_spin_wheel_black
    }

    override fun getViewModel(): SpinWheelProductViewModel {
        mViewModel = ViewModelProvider(this).get(SpinWheelProductViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val intent = intent
        orderId = intent.getStringExtra(AppConstants.ORDER_ID)
//        val args = intent.getBundleExtra("BUNDLE")
//        val getList = args!!.getSerializable("ARRAYLIST") as ArrayList<SectionListItem>


        setObserver()
        setToolbarAndTitle(
            context = this@SpinWheelViewDark,
            toolbar = toolbar,
            isBackArrowVisible = true, titleColor = Color.WHITE
        )

//        Glide.with(applicationContext).load(R.raw.coin).into(coin)
        dialogDialog = Dialog(this)

        luckyWheelView.setRound(4)

        if (sectionArrayList.size == 0) {
            mViewModel.callProductsDetailsApi(orderId)
        } else {
            mViewModel.setDataInSpinWheel(sectionArrayList)
            luckyWheelView.setData(mViewModel.luckyItemList)

        }



        try {
            luckyWheelView.setLuckyRoundItemSelectedListener {
                mViewModel.coinVisibilty.set(true)
                showBurnDialog()

            }
        } catch (e: Exception) {

        }

        mViewModel.enableSpin.value = false

    }


    internal fun showBurnDialog() {


        dialogDialog?.setCancelable(false)
        dialogDialog?.setCanceledOnTouchOutside(false)
        dialogDialog?.setContentView(R.layout.dialog_cash_won)

        val wlp = dialogDialog?.window?.attributes

        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT

        dialogDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogDialog?.window?.attributes = wlp


        dialogDialog?.clicked?.setOnClickListener(View.OnClickListener {
            finish()

        })


        dialogDialog?.show()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.spinWheelResponseList.observe(this) {
            try {
                when (it.sectionId) {
                    1 -> {
                        luckyWheelView.startLuckyWheelWithTargetIndex(it.sectionId!! - 1)

                    }
                    else -> {

                        luckyWheelView.startLuckyWheelWithTargetIndex(it.sectionId!! - 1)
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
        mViewModel.redeemCallBackResponse.observe(this) {
            it.sectionList?.forEach { i ->
                if (i != null) {
                    sectionArrayList.add(i)
                }
            }


            mViewModel.setDataInSpinWheel(sectionArrayList)
            luckyWheelView.setData(mViewModel.luckyItemList)

        }

        mViewModel.onError.observe(this)
        {
            if (it.errorCode != "UAA_1058") {
                callErrorBottomSheet(AppConstants.ERROR_TYPE_SPIN_ALLOWED, message = it.msg)

            }

        }

        mViewModel.onPlayClicked.observe(this)
        {

            mViewModel.callSpinWheelApi(orderId)

        }


    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onDestroy() {
        super.onDestroy()
        setResult(52)
    }


    private fun callErrorBottomSheet(type: String?, message: String? = null) {

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            val bottomSheet =
                ErrorBottomSpinProductSheet(type!!, message, this, mViewModel)
            bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
            bottomSheet.show(supportFragmentManager, "ErrorBottomSheet")
        }


    }

    override fun onSpinErrorClick(type: String) {

    }



}