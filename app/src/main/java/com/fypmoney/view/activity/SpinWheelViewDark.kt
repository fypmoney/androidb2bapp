package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewSpinWheelBlackBinding
import com.fypmoney.model.RedeemDetailsResponse
import com.fypmoney.model.SectionListItem
import com.fypmoney.model.aRewardProductResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.ErrorBottomSheet
import com.fypmoney.view.fragment.RedeemMyntsBottomSheet
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.viewmodel.SpinWheelViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_spin_wheel_black.*


import java.util.*

/*
* This class is used for spin the wheel
* */
class SpinWheelViewDark : BaseActivity<ViewSpinWheelBlackBinding, SpinWheelViewModel>(),
    ErrorBottomSheet.OnSpinErrorClickListener {
    private var bottomSheetMessage: RedeemMyntsBottomSheet? = null
    private var showerror: Boolean? = null
    private lateinit var mViewModel: SpinWheelViewModel

    companion object {
        var itemsArrayList: ArrayList<SectionListItem> = ArrayList()
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_spin_wheel_black
    }

    override fun getViewModel(): SpinWheelViewModel {
        mViewModel = ViewModelProvider(this).get(SpinWheelViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        val intent = intent
//        val args = intent.getBundleExtra("BUNDLE")
//        val getList = args!!.getSerializable("ARRAYLIST") as ArrayList<SectionListItem>

        mViewModel.setDataInSpinWheel(itemsArrayList)
        setObserver()
        setToolbarAndTitle(
            context = this@SpinWheelViewDark,
            toolbar = toolbar,
            isBackArrowVisible = true
        )

//        Glide.with(applicationContext).load(R.raw.coin).into(coin)
        luckyWheelView.setData(mViewModel.luckyItemList)
        luckyWheelView.setRound(4)
        mViewModel.callGetRewardsApi()
        try {
            luckyWheelView.setLuckyRoundItemSelectedListener {
                mViewModel.coinVisibilty.set(true)
                mViewModel.spinnerClickable.set(true)
                if (showerror!!) {
                    callErrorBottomSheet(
                        AppConstants.ERROR_TYPE_AFTER_SPIN,
                        message = getString(R.string.spin_wheel_error)
                    )

                }
            }
        } catch (e: Exception) {

        }

        mViewModel.enableSpin.value = false

    }

    private fun callRedeemMyntsSheet(redeemDetails: RedeemDetailsResponse) {
        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {
                bottomSheetMessage?.dismiss()
            }

            override fun onCallClicked(pos: Int) {
                mViewModel.callRedeemCoins()
                bottomSheetMessage?.dismiss()

            }
        }
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            bottomSheetMessage =
                RedeemMyntsBottomSheet(itemClickListener2, redeemDetails)
            bottomSheetMessage?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
            bottomSheetMessage?.show(supportFragmentManager, "TASKMESSAGE")
        }
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
                        showerror = true

//                        Handler(Looper.getMainLooper()).postDelayed({
//
//                        }, 3000)

                    }
                    else -> {
                        showerror = false
                        luckyWheelView.startLuckyWheelWithTargetIndex(it.sectionId!! - 1)
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }


        mViewModel.onError.observe(this)
        {
            if (it.errorCode != "UAA_1058") {
                callErrorBottomSheet(AppConstants.ERROR_TYPE_SPIN_ALLOWED, message = it.msg)

            }

        }

        mViewModel.onPlayClicked.observe(this)
        {
            when (mViewModel.spinAllowed.value) {
                AppConstants.NO -> {

                    callErrorBottomSheet(
                        AppConstants.ERROR_TYPE_SPIN_ALLOWED,
                        message = getString(R.string.better_luck)
                    )

                    Handler(Looper.getMainLooper()).postDelayed({
                        mViewModel.spinnerClickable.set(true)
                    }, 1500)

                    mViewModel.coinVisibilty.set(true)

                }
                else -> {

                    mViewModel.callSpinWheelApi()

                }
            }
        }



        mViewModel.onRewardsHistoryClicked.observe(this)
        {
            if (it) {
                intentToActivity(RewardsHistoryView::class.java)
                mViewModel.onRewardsHistoryClicked.value = false

            }
        }

        /*    mViewModel.onSpinDone.observe(this)
            {
                if (it) {
                    when (mViewModel.spinWheelResponseList.value?.sectionId?.equals(1)) {

                    }
                    val index = mViewModel.spinWheelResponseList.value?.sectionId
                    if (index != null) {
                        luckyWheelView.startLuckyWheelWithTargetIndex(index)
                    }
                }
            }
            mViewModel.onGetRewardsSuccess.observe(this)
            {
                if (it) {
                    when (mViewModel.getRewardsResponseList.get()?.spinAllowedToday) {
                        AppConstants.YES -> {
                            if (mViewModel.enableSpin.value == true) {
                                mViewModel.callSpinWheelApi()
                                mViewModel.enableSpin.value = false
                            }
                        }
                    }

                    when (mViewModel.getRewardsResponseList.get()?.spinAllowedToday) {
                        AppConstants.NO -> {
                            if (mViewModel.enableSpin.value == true) {
                                mViewModel.callSpinWheelApi()
                                mViewModel.enableSpin.value = false
                            }
                            *//*callErrorBottomSheet(
                            AppConstants.ERROR_TYPE_SPIN_ALLOWED,
                            message = getString(R.string.better_luck)
                        )*//*
                    }
                }
                mViewModel.onGetRewardsSuccess.value = false

            }
*/


    }

    /*
   * This method is used to call log out
   * */
    private fun callErrorBottomSheet(type: String?, message: String? = null) {

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            val bottomSheet =
                ErrorBottomSheet(type!!, message, this, mViewModel)
            bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
            bottomSheet.show(supportFragmentManager, "ErrorBottomSheet")
        }


    }

    override fun onSpinErrorClick(type: String) {
        /*   when (type) {
               AppConstants.ERROR_TYPE_SPIN_ALLOWED -> {
                   finish()
               }
           }
   */

    }



    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(this@SpinWheelViewDark, aClass)
        startActivity(intent)
    }
}